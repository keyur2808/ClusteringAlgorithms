package edu.buffalo.cse601.datamining.project2.kmeansMR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import edu.buffalo.cse601.datamining.project2.datastructures.DistanceMatrix;
import edu.buffalo.cse601.datamining.project2.distancemeasures.DistanceMeasure;
import edu.buffalo.cse601.datamining.project2.distancemeasures.Euclidean;
import edu.buffalo.cse601.datamining.project2.externalIndex.JaccardCoefficient;
import edu.buffalo.cse601.datamining.project2.externalIndex.RandIndex;
import edu.buffalo.cse601.datamining.project2.inputReader.ReadIn;
import edu.buffalo.cse601.datamining.project2.internalIndex.InternalIndex;
import edu.buffalo.cse601.datamining.project2.plots.PCAPlot;
import edu.buffalo.cse601.datamining.project2.pojo.Point;
import edu.buffalo.cse601.datamining.project2.utils.GetGeneMap;
import edu.buffalo.cse601.datamining.project2.utils.PropertyReader;

public class PlotClustersPCA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Properties properties = PropertyReader.getProperties("KMeansMR.properties");
		String filename = (String) properties.get("FILENAME");
		int numClusters = Integer.parseInt((String) properties.get("NUMCLUSTERS"));
		String filePath = "Input/" + filename;
		ArrayList<Point> points = new ArrayList<Point>();
		TreeMap<Integer, Integer> groundTruth = ReadIn.readIn(filePath, points);
		TreeMap<Integer, List<Double>> geneMap = GetGeneMap.getGeneMap(points);
		double clusterCoords[][] = new double[numClusters][points.get(0).getCoords().length];
		DistanceMeasure dm = new Euclidean();
		TreeMap<Integer, Integer> clusteringSolution = new TreeMap<Integer, Integer>();

		File inFile = new File("Results/" + numClusters + "/centroids");
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(inFile.getAbsoluteFile());
			br = new BufferedReader(fr);
			String line;
			for (int i = 0; (line = br.readLine()) != null && i < numClusters; i++) {
				String[] lineArray = line.split(":");
				int clusterId = Integer.valueOf(lineArray[0]);
				String[] coordsString = lineArray[1].split(",");
				for (int j = 0; j < coordsString.length; j++) {
					clusterCoords[clusterId][j] = Double.valueOf(coordsString[j]);
				}
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

		for (Point point : points) {
			double minDist = Double.MAX_VALUE;
			double currDist = 0.0f;
			int memberOf = -1;

			// compute membership of each datapoint
			for (int i = 0; i < numClusters; i++) {
				double[] clusterCoord = clusterCoords[i];
				currDist = dm.getDistance(clusterCoord, point.getCoords());
				minDist = (currDist < minDist) ? currDist : minDist;
				if (minDist == currDist)
					memberOf = i;
			}
			clusteringSolution.put(Integer.valueOf(point.getGeneId()), memberOf);
		}

		Set<Integer> keys = clusteringSolution.keySet();
		TreeMap<Integer, ArrayList<Integer>> clusters = new TreeMap<Integer, ArrayList<Integer>>();

		for (Integer key : keys) {
			int cluster = clusteringSolution.get(key);
			ArrayList<Integer> list;
			if (clusters.containsKey(cluster)) {
				list = clusters.get(cluster);
			} else {
				list = new ArrayList<Integer>();
			}
			list.add(key);
			clusters.put(cluster, list);
		}

		JaccardCoefficient jc = new JaccardCoefficient(groundTruth, clusteringSolution);
		double jaccardCoeff = jc.evaluate();

		System.out.println("Jaccard Coefficient: " + jaccardCoeff);

		RandIndex ri = new RandIndex(groundTruth, clusteringSolution);
		double randIndex = ri.evaluate();

		System.out.println("Rand Index: " + randIndex);

		for (Integer cluster : clusters.keySet()) {
			System.out.println("Cluster Id: " + cluster + " \t Cluster size: " + clusters.get(cluster).size());
		}

		DistanceMatrix matrix = new DistanceMatrix();
		double[][] distanceMatrix = matrix.generateDistanceMatrix(geneMap);

		InternalIndex idx = new InternalIndex(distanceMatrix, clusteringSolution);
		idx.evaluate();

		System.out.println(clusters);

		// Plots
		PCAPlot plot = new PCAPlot(filePath);
		plot.run();
		PCAPlot pl = new PCAPlot(geneMap, "kMeansMR", clusteringSolution);
		pl.run();
	}
}
