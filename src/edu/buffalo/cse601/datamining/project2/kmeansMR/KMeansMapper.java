package edu.buffalo.cse601.datamining.project2.kmeansMR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.buffalo.cse601.datamining.project2.distancemeasures.Euclidean;
import edu.buffalo.cse601.datamining.project2.pojo.Point;

public class KMeansMapper extends Mapper<Object, Text, IntWritable, Text> {
	private static final transient Logger LOG = LoggerFactory.getLogger(KMeansMapper.class);

	ArrayList<Cluster> clusterList = new ArrayList<Cluster>();

	protected void setup(Context context) {
		// get the initial centroid
		try {
			Path centroid_path = new Path("/conf/centroids");
			FileSystem fs = FileSystem.get(new Configuration());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(centroid_path)));
			String centroidData = null;
			String[] mainTokens = null;
			String[] tokens = null;
			Cluster clust = null;
			while ((centroidData = br.readLine()) != null) {
				LOG.info(centroidData);
				mainTokens = centroidData.split(":");
				tokens = mainTokens[1].split(",");
				DoubleWritable[] coords = new DoubleWritable[tokens.length];
				for (int i = 0; i < tokens.length; i++) {
					coords[i] = new DoubleWritable(Double.valueOf(tokens[i]));
				}
				clust = new Cluster(new IntWritable(Integer.valueOf(mainTokens[0])), coords);
				clusterList.add(clust);
			}
			br.close();
		} catch (IOException iex) {
			iex.printStackTrace();
		}
	}

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		// the mapper is sent n-lines at a time -- not the whole document
		String doc = value.toString();
		// if suppose the mapper was given N lines --- we shall split it this way
		String[] lines = doc.split("\n");

		for (String line : lines) {

			String[] contents = line.split("\t");
			String geneId = contents[0];
			double[] coords = new double[(contents.length - 2)];
			for (int i = 2; i < contents.length; i++) {
				coords[i - 2] = Double.valueOf(contents[i]);
			}
			Point dataPoint = new Point();
			dataPoint.setGeneId(geneId);
			dataPoint.setCoords(coords);

			double minDist = Double.MAX_VALUE;
			double currDist = 0.0f;
			int memberOf = -1;

			// compute membership of each datapoint
			for (int i = 0; i < clusterList.size(); i++) {
				Cluster cluster = clusterList.get(i);
				DoubleWritable[] clusterCoords = cluster.getCoords();
				double[] centroidCoords = new double[clusterCoords.length];
				for(int j = 0; j < clusterCoords.length; j++){
					centroidCoords[j] = clusterCoords[j].get();
				}
				Euclidean eu = new Euclidean();
				currDist = eu.getDistance(centroidCoords, dataPoint.getCoords());
//				currDist = euclid_dist(cluster, dataPoint);
				if(currDist < minDist){
					minDist = currDist;
					memberOf = cluster.getCentroidID().get();
				}
				/*minDist = (currDist < minDist) ? currDist : minDist;
				if (minDist == currDist)
					memberOf = cluster.getCentroidID().get();*/
			}
			System.out.println("geneId: " + geneId + "\t Cluster id: " + memberOf);
			dataPoint.setDistFromCentroid(minDist);
			Text c = new Text(dataPoint.toString());
			context.write(new IntWritable(memberOf), c);
		}
	}

	private double euclid_dist(Cluster centroid, Point dataPoint) {
		//		System.out.println("Centroid id: " + centroid.getCentroidID());
		DoubleWritable[] centroidCoords = centroid.getCoords();
		double[] coords = dataPoint.getCoords();
		double sum = 0;
		for (int i = 0; i < centroidCoords.length; i++) {
			//			System.out.println("centroidCoords[" + i + "]" + centroidCoords[i]);
			//			System.out.println("coords[" + i + "]" + coords[i]);
			sum += Math.pow(centroidCoords[i].get() - coords[i], 2);
		}
		double dist = Math.sqrt(sum);
		return dist;
	}
}