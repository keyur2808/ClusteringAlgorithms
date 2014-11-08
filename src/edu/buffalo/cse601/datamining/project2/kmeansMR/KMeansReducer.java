package edu.buffalo.cse601.datamining.project2.kmeansMR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.buffalo.cse601.datamining.project2.kmeansMR.KMeansDriver.Kmeans_iterations;

public class KMeansReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
	HashMap<Integer, Cluster> initClusters = new HashMap<Integer, Cluster>();
	HashMap<Integer, Cluster> myClusters = new HashMap<Integer, Cluster>();

	private static final transient Logger LOG = LoggerFactory.getLogger(KMeansReducer.class);

	public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		int memberCount = 0;
		String tokens[] = null;

		double clusterQuality = 0.0f;
		double[] sums = null;
		int dim = 0;

		Iterator<Text> it = values.iterator();
		List<Text> cache = new ArrayList<Text>();

		int count = 0;
		// first loop and caching
		while (it.hasNext()) {
			Text val = it.next();
			tokens = val.toString().split(";");
			String coordsString = tokens[0];
			String[] coordArray = coordsString.split(",");
			dim = coordArray.length;
			cache.add(val);
			if(count == 0){
				sums = new double[dim];
			}
			count++;
		}
		System.out.println("Key in reducer: " + key);

		for (Text val : cache) {
			// tokenize the values
			tokens = val.toString().split(";");
			String coordsString = tokens[0];
			String[] coordArray = coordsString.split(",");
			dim = coordArray.length;
			for (int i = 0; i < coordArray.length; i++) {
				sums[i] += Double.valueOf(coordArray[i]);
				System.out.println("coordArray[" + i + "]: " + coordArray[i]);
				System.out.println("sums[" + i + "]: " + sums[i]);
			}
			context.write(key, val);
			clusterQuality += Double.valueOf(tokens[1]);
			if (tokens.length == 3) {
				memberCount += Integer.valueOf(tokens[2]);
			} else {
				memberCount++;
			}
		}

		/*
		 * create cluster object
		 */
		DoubleWritable[] coords = new DoubleWritable[dim];
		for (int i = 0; i < dim; i++) {
			coords[i] = new DoubleWritable(sums[i] / memberCount);
		}

		Cluster cluster = new Cluster(new IntWritable(key.get()), coords);
		//		cluster.getClusterQuality().set(clusterQuality / memberCount);
		cluster.getClusterQuality().set(clusterQuality);
		cluster.getMemberCount().set(memberCount);

		System.out.println("Cluster id in reducer: " + cluster.getCentroidID().get());
		myClusters.put(key.get(), cluster);
	}

	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);

		// find the location of file that holds the centroids
		Configuration conf = context.getConfiguration();
		Path centroid_path = new Path("/conf/centroids");
		FileSystem fs = FileSystem.get(conf);
		fs.delete(centroid_path, true);

		System.out.println("initial clusters");
		System.out.println(initClusters);
		System.out.println("new clusters");
		System.out.println(myClusters);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs.create(centroid_path, true)));
		String centroid_line = "";
		Cluster clst = null;

		for (int i = 0; i < initClusters.size(); i++) {
			if (myClusters.containsKey(i)) {
				clst = myClusters.get(i);
				centroid_line = clst.toString();
				bw.write(centroid_line + "\n");
			} else {
				clst = initClusters.get(i);
				clst.getMemberCount().set(0.0);
				clst.getClusterQuality().set(0.0);
				int dim = clst.getCoords().length;
				DoubleWritable[] newCoords = new DoubleWritable[dim];
				for (int j = 0; j < dim; j++) {
					newCoords[j] = new DoubleWritable(0);
				}
				clst.setCoords(newCoords);
				centroid_line = clst.toString();
				bw.write(centroid_line + "\n");
			}
		}

		/*for (int i = 0; i < myClusters.size(); i++) {
			clst = myClusters.get(myClusters.keySet().toArray()[i]);
			centroid_line = clst.toString();
			bw.write(centroid_line + "\n");
		}*/

		bw.close();

		//check if converged
		for (int i = 0; i < initClusters.size(); i++) {
			if (myClusters.containsKey(i)) {
				if (!hasConverged(initClusters.get(i), myClusters.get(i))) {
					context.getCounter(Kmeans_iterations.moreIterations).increment(1L);
				}
			}
		}
	}

	protected void setup(Context context) {
		// learn the initial centroid
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
				double clusterQuality = Double.valueOf(mainTokens[2]);
				double memberCount = Double.valueOf(mainTokens[3]);
				clust = new Cluster(new IntWritable(Integer.valueOf(mainTokens[0])), coords);
				clust.getClusterQuality().set(clusterQuality);
				clust.getMemberCount().set(memberCount);
				initClusters.put(Integer.valueOf(mainTokens[0]), clust);
			}

			br.close();
		} catch (IOException iex) {
			iex.printStackTrace();
		}
	}

	private boolean hasConverged(Cluster A, Cluster B) {
		DoubleWritable[] ACoords = A.getCoords();
		DoubleWritable[] BCoords = B.getCoords();
		double sum = 0;
		for (int i = 0; i < ACoords.length; i++) {
			sum += Math.pow(ACoords[i].get() - BCoords[i].get(), 2);
		}
		Double dist = Math.sqrt(sum);
		System.out.println("Distance between previous and new centroids for cluster: " + A.getCentroidID().get() + " or " + B.getCentroidID().get() + " is \t" + dist);
		if (new Double(0.0).compareTo(dist) >= 0)
			return true;
		else
			return false;
	}
}