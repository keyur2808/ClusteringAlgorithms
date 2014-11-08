package edu.buffalo.cse601.datamining.project2.kmeansMR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.buffalo.cse601.datamining.project2.inputReader.ReadIn;
import edu.buffalo.cse601.datamining.project2.pojo.Point;
import edu.buffalo.cse601.datamining.project2.utils.PropertyReader;

public class KMeansDriver {
	private static final transient Logger LOG = LoggerFactory.getLogger(KMeansDriver.class);

	static enum Kmeans_iterations {
		moreIterations;
	}

	public static void main(String args[]) {
		KMeansDriver kMeansDriver = new KMeansDriver();
		Properties properties = PropertyReader.getProperties("KMeansMR.properties");
		String filename = "dataset1_norm.txt";
		String centroidsFile = (String) properties.get("CENTROIDSFILE");
		int numClusters = Integer.parseInt((String) properties.get("NUMCLUSTERS"));
		int maxIter = -1;
		if (properties.get("MAXITER") != null) {
			maxIter = Integer.parseInt((String) properties.get("MAXITER"));
		}

		String filePath = "Input/" + filename;
		ArrayList<Integer> fileCentroids = null;
		if (centroidsFile != null) {
			fileCentroids = readFile(centroidsFile);
		}

		ArrayList<Point> points = new ArrayList<Point>();

		ReadIn.readIn(filePath, points);
		int numDimensions = points.get(0).getCoords().length;
		if (args.length < 1) {
			System.out.println("Well we need atleast 1 argument i.e the number of clusters we need to find");
			return;
		}

		// getting commandline args and using them
		int k = Integer.valueOf(args[0]);
		int reducetasks = 1; // default value assigned
		Cluster[] centroids = new Cluster[k];

		Path centroid_path = new Path("/conf/centroids");

		/**
		 * randomly create centroids and have it written to a file and have the
		 * mappers access the file
		 */
		try {

			FileSystem fs = FileSystem.get(new Configuration());
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs.create(centroid_path, true)));
			String centroid_line = "";
			double[][] initialCentroids = null;
			if (centroidsFile == null)
				initialCentroids = kMeansDriver.generateInitialMeanVectors(numClusters, points, numDimensions);
			else
				initialCentroids = kMeansDriver.generateInitialMeanVectors(fileCentroids, numClusters, points, numDimensions);
			// seeding the k centroids
			for (int i = 0; i < k; i++) {
				centroids[i] = new Cluster(numDimensions);
				centroids[i].setCentroidID(new IntWritable(i));
				DoubleWritable[] coords = new DoubleWritable[numDimensions];

				// initialize cluster centroids

				for (int j = 0; j < numDimensions; j++) {
					/*
					 * int sgn = 1; if (Math.random() < 0.5) sgn = -1; coords[j]
					 * = new DoubleWritable(sgn * Math.random() * 4);
					 */
					coords[j] = new DoubleWritable(initialCentroids[i][j]);
				}

				centroids[i].setCoords(coords);
				centroid_line = centroids[i].toString();
				// System.out.println(centroid_line);
				bw.write(centroid_line + "\n");
				centroid_line = "";
			}
			bw.close();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		Configuration conf = new Configuration();
		conf.set("centroid.path", "/conf/centroids");
		LOG.info("HDFS Root Path: {}", conf.get("fs.defaultFS"));
		LOG.info("MR Framework: {}", conf.get("mapreduce.framework.name"));

		// start map reduce
		// boolean notConverged = true;
		long result = 1;
		int iterationCount = 0;
		while (result > 0) {
			if (maxIter != -1) {
				if (iterationCount >= maxIter)
					break;
			}
			System.out.println("The number of centroids = " + k);
			System.out.println("============================================================");
			System.out.println("=======================      iteration::" + (iterationCount + 1) + "        =======================");
			System.out.println("============================================================");

			try {
				properties.load(KMeansDriver.class.getResourceAsStream("/conf/configuration.properties"));
				LOG.info(properties.getProperty("output_path"));
				kMeansDriver.deleteFolder(conf, properties.getProperty("output_path"));
			} catch (IOException iex) {
				iex.printStackTrace();
				System.out.println("IOE exception encountered");
			}

			try {
				// set job details
				Job job = Job.getInstance(conf);
				job.setNumReduceTasks(reducetasks);
				job.setJarByClass(KMeansDriver.class);
				job.setMapperClass(KMeansMapper.class);
				job.setCombinerClass(KMeansCombiner.class);
				job.setReducerClass(KMeansReducer.class);
				job.setOutputKeyClass(IntWritable.class);
				job.setOutputValueClass(Text.class);

				// copyInitialCentroidConfiguration(job,centroid_path);
				// location of input and where to output
				FileInputFormat.addInputPath(job, new Path(properties.getProperty("input_path")));
				FileOutputFormat.setOutputPath(job, new Path(properties.getProperty("output_path")));

				job.waitForCompletion(true);

				Counters jobCntrs = job.getCounters();
				result = jobCntrs.findCounter(Kmeans_iterations.moreIterations).getValue();

				System.out.println("============================================================");
				System.out.println("=================iterationEND::" + (iterationCount + 1) + "=============");
				System.out.println("============================================================");
				iterationCount++;

				try {
					FileSystem fs = FileSystem.get(conf);
					BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(centroid_path)));
					String line = null;

					System.out.println("************* Current centroids and membership START *********");
					while ((line = br.readLine()) != null) {
						System.out.println(line);
					}
					System.out.println("************* Current centroids and membership END *********");
					br.close();
				} catch (IOException ioex) {
					ioex.printStackTrace();
				}

			} catch (IOException iex) {
				iex.printStackTrace();
			} catch (InterruptedException inex) {
				inex.printStackTrace();
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			}
		}// end of while
		try {
			FileSystem fs = FileSystem.get(conf);
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(centroid_path)));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs.append(centroid_path)));

			String line = null;
			String[] tokens = null;
			double clst_overall = 0.0f;

			while ((line = br.readLine()) != null) {
				tokens = line.split(":");
				clst_overall += Double.valueOf(tokens[2]);
			}
			// clst_overall = clst_overall / k;
			bw.write("the cluster quality::" + clst_overall + "\n");
			bw.write("The number of iterations::" + iterationCount + "\n");
			bw.flush();
			bw.close();
			br.close();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	private static ArrayList<Integer> readFile(String centroidsFile) {
		ArrayList<Integer> centroids = new ArrayList<Integer>();
		File inFile = new File(centroidsFile);
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(inFile.getAbsoluteFile());
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				centroids.add(Integer.valueOf(line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return centroids;
	}

	private void deleteFolder(Configuration conf, String folderPath) throws IOException {
		LOG.info("Called delete");
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(folderPath);
		if (fs.exists(path)) {
			fs.delete(path, true);
		}
	}

	private double[][] generateInitialMeanVectors(ArrayList<Integer> fileCentroids, int numClusters, ArrayList<Point> points, int numDimensions) {
		double[][] centroids = new double[numClusters][numDimensions];
		for (int i = 0; i < numClusters; i++) {
			int randomNumber = fileCentroids.get(i);
			System.out.println("Gene Id from file: " + randomNumber + " \t " + points.get(randomNumber));
			System.out.println();
			centroids[i] = points.get(randomNumber).getCoords();
		}
		return centroids;
	}

	private double[][] generateInitialMeanVectors(int numClusters, ArrayList<Point> points, int numDimensions) {
		double[][] centroids = new double[numClusters][numDimensions];
		Random random = new Random();
		for (int i = 0; i < numClusters; i++) {
			int randomNumber = random.nextInt(points.size());
			System.out.println("Random Number: " + randomNumber + " \t " + points.get(randomNumber));
			System.out.println();
			centroids[i] = points.get(randomNumber).getCoords();
		}
		return centroids;
	}

	/*
	 * private static void copyInitialCentroidConfiguration(Job job, Path
	 * centroid_path) throws IOException { Configuration conf =
	 * job.getConfiguration(); FileSystem fs = FileSystem.get(conf);
	 * 
	 * // upload the file to hdfs. Overwrite any existing copy.
	 * fs.copyToLocalFile(false, centroid_path, new
	 * Path("/home/hduser/initialConf")); }
	 */
}
