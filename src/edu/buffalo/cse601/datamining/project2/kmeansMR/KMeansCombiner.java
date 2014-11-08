package edu.buffalo.cse601.datamining.project2.kmeansMR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class KMeansCombiner extends Reducer<IntWritable, Text, IntWritable, Text> {
	HashMap<Integer, Cluster> initClusters = new HashMap<Integer, Cluster>();
	HashMap<Integer, Cluster> myClusters = new HashMap<Integer, Cluster>();

	//	private static final transient Logger LOG = LoggerFactory.getLogger(KMeansCombiner.class);

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
			// split value ;
			memberCount++; // members of a cluster
			// tokenize the values
			tokens = val.toString().split(";");
			String coordsString = tokens[0];
			String[] coordArray = coordsString.split(",");
			dim = coordArray.length;
			for (int i = 0; i < coordArray.length; i++) {
				System.out.println("coordArray[" + i + "]: " + coordArray[i]);
				sums[i] += Double.valueOf(coordArray[i]);
				System.out.println("sums[" + i + "]: " + sums[i]);
			}
			clusterQuality += Double.valueOf(tokens[1]);
		}
		StringBuffer sumString = new StringBuffer();
		for (int i = 0; i < dim; i++) {
			sumString.append(sums[i]);
			sumString.append(",");
		}
		sumString.deleteCharAt(sumString.length() - 1);
		sumString.append(";");
		sumString.append(clusterQuality);
		sumString.append(";");
		sumString.append(memberCount);
		context.write(key, new Text(sumString.toString()));
	}
}
