package edu.buffalo.cse601.datamining.project2.dbScan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import edu.buffalo.cse601.datamining.project2.datastructures.DistanceMatrix;
import edu.buffalo.cse601.datamining.project2.distancemeasures.Cosine;
import edu.buffalo.cse601.datamining.project2.distancemeasures.DistanceMeasure;
import edu.buffalo.cse601.datamining.project2.distancemeasures.Euclidean;
import edu.buffalo.cse601.datamining.project2.externalIndex.JaccardCoefficient;
import edu.buffalo.cse601.datamining.project2.externalIndex.RandIndex;
import edu.buffalo.cse601.datamining.project2.inputReader.ReadIn;
import edu.buffalo.cse601.datamining.project2.internalIndex.InternalIndex;
import edu.buffalo.cse601.datamining.project2.plots.PCAPlot;
import edu.buffalo.cse601.datamining.project2.pojo.Point;
import edu.buffalo.cse601.datamining.project2.utils.GetGeneMap;
import edu.buffalo.cse601.datamining.project2.utils.Normalizer;
import edu.buffalo.cse601.datamining.project2.utils.PropertyReader;

public class DBScan {

	private TreeMap<Integer, Integer> groundTruth = new TreeMap<Integer, Integer>();
	private DistanceMeasure distanceMeasure;

	public TreeMap<Integer, Integer> main(String filename, String distanceType, double eps, int minPts, ArrayList<Double> indexes) {

		switch (distanceType) {
		case "EUCLIDEAN": {
			distanceMeasure = new Euclidean();
			break;
		}
		case "COSINE": {
			distanceMeasure = new Cosine();
			break;
		}
		default: {

		}
		}

		//		int numDimensions = 0;

		String filePath = filename;
		ArrayList<Point> points = new ArrayList<Point>();

		groundTruth = ReadIn.readIn(filePath, points);
		TreeMap<Integer, List<Double>> geneMap = GetGeneMap.getGeneMap(points);

		findKink(points, 5);

		TreeMap<Integer, Integer> clusteringSolution = dbScan(points, eps, minPts);
		TreeMap<Integer, ArrayList<Integer>> clusters = new TreeMap<Integer, ArrayList<Integer>>();

		Set<Integer> keys = clusteringSolution.keySet();

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

		List<Integer> keyList = new ArrayList<Integer>(keys);

		Collections.sort(keyList);

		for (Integer entry : keyList) {
			Integer geneId = entry;
			Integer clusterId = clusteringSolution.get(entry);

			System.out.println("Gene Id: " + geneId + "\t Cluster Id from solution: " + clusterId + "\t Ground truth cluster: " + groundTruth.get(geneId));
		}

		JaccardCoefficient jc = new JaccardCoefficient(groundTruth, clusteringSolution);
		double jaccardCoeff = jc.evaluate();
		indexes.set(0, jaccardCoeff);

		System.out.println("Jaccard Coefficient: " + jaccardCoeff);

		RandIndex ri = new RandIndex(groundTruth, clusteringSolution);
		double randIndex = ri.evaluate();

		System.out.println("Rand Index: " + randIndex);

		DistanceMatrix matrix = new DistanceMatrix();
		double[][] distanceMatrix = matrix.generateDistanceMatrix(geneMap);

		InternalIndex idx = new InternalIndex(distanceMatrix, clusteringSolution);
		indexes.set(1, idx.evaluate());
		
		System.out.println(clusters);

		// Plots
		Thread plot = new Thread(new PCAPlot(filePath));
		plot.start();
		Thread pl = new Thread(new PCAPlot(geneMap, "DBScan", clusteringSolution));
		pl.start();

		return clusteringSolution;
	}

	public static void main(String[] args) {
		Properties properties = PropertyReader.getProperties("DBScan.properties");
		String filename = (String) properties.get("FILENAME");
		String distanceType = (String) properties.get("DISTANCEMEASURE");
		double eps = Double.parseDouble((String) properties.get("EPS"));
		int minPts = Integer.parseInt((String) properties.get("MINPTS"));
		DBScan dbScan = new DBScan();

		switch (distanceType) {
		case "EUCLIDEAN": {
			dbScan.distanceMeasure = new Euclidean();
			break;
		}
		case "COSINE": {
			dbScan.distanceMeasure = new Cosine();
			break;
		}
		default: {

		}
		}

		//		int numDimensions = 0;

		String filePath = filename;
		ArrayList<Point> points = new ArrayList<Point>();

		dbScan.groundTruth = ReadIn.readIn(filePath, points);
		// Normalize
//		int numDimensions = points.get(0).getCoords().length;
//		points = Normalizer.normalize(points, numDimensions);
		TreeMap<Integer, List<Double>> geneMap = GetGeneMap.getGeneMap(points);

		int k = 5;
		double newEps = dbScan.findKink(points, k);

		System.out.println("From graph: k: " + k + "\t minEps: " + newEps);

		TreeMap<Integer, Integer> clusteringSolution = dbScan.dbScan(points, eps, minPts);
		HashMap<Integer, ArrayList<Integer>> clusters = new HashMap<Integer, ArrayList<Integer>>();

		Set<Integer> keys = clusteringSolution.keySet();

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

		List<Integer> keyList = new ArrayList<Integer>(keys);

		Collections.sort(keyList);

		for (Integer entry : keyList) {
			Integer geneId = entry;
			Integer clusterId = clusteringSolution.get(entry);

			System.out.println("Gene Id: " + geneId + "\t Cluster Id from solution: " + clusterId + "\t Ground truth cluster: " + dbScan.groundTruth.get(geneId));
		}

		JaccardCoefficient jc = new JaccardCoefficient(dbScan.groundTruth, clusteringSolution);
		double jaccardCoeff = jc.evaluate();

		System.out.println("Jaccard Coefficient: " + jaccardCoeff);

		RandIndex ri = new RandIndex(dbScan.groundTruth, clusteringSolution);
		double randIndex = ri.evaluate();

		System.out.println("Rand Index: " + randIndex);

		DistanceMatrix matrix = new DistanceMatrix();
		double[][] distanceMatrix = matrix.generateDistanceMatrix(geneMap);

		InternalIndex idx = new InternalIndex(distanceMatrix, clusteringSolution);
		idx.evaluate();
		
		for (Integer cluster : clusters.keySet()) {
			System.out.println("Cluster Id: " + cluster + " \t Cluster size: " + clusters.get(cluster).size());
		}

		System.out.println(clusters);
		
		// Plots
		//Thread t = new Thread(new PCAPlot(filePath));
		//plot.run();

		Thread pl = new Thread(new PCAPlot(geneMap, "DBScan", clusteringSolution));
		pl.run();
		
		DBSCANClusterer<DoublePoint> DBSCANClusterer = new DBSCANClusterer<DoublePoint>(eps, minPts - 1, new EuclideanDistance());
		ArrayList<DoublePoint> doublePoints = new ArrayList<DoublePoint>();
		for (Point point : points) {
			doublePoints.add(new DoublePoint(point.getCoords()));
		}
		List<Cluster<DoublePoint>> listofClusters = DBSCANClusterer.cluster(doublePoints);
		int count = 1;
		for (Cluster<DoublePoint> cluster : listofClusters) {
			System.out.println("Cluster Id: " + count + " \t Cluster size: " + cluster.getPoints().size());
			count++;
		}
		
	}

	private double findKink(ArrayList<Point> points, int k) {
		List<Double> kNNDistances = new ArrayList<Double>();
		for (Point point : points) {
			List<Double> distances = new ArrayList<Double>();
			for (Point point2 : points) {
				if (!point.equals(point2)) {
					double distance = distanceMeasure.getDistance(point.getCoords(), point2.getCoords());
					distances.add(distance);
				}
			}
			Collections.sort(distances);
			double distToN = distances.get(k - 1);
			kNNDistances.add(distToN);
		}
		Collections.sort(kNNDistances);
		System.out.println("Start");
		double minEps = Double.MAX_VALUE;
		double minSlope = Double.MAX_VALUE;
		double prevDistance = 0;
		for (Double distance : kNNDistances) {
			if (distance - prevDistance < minSlope) {
				minSlope = distance - prevDistance;
				minEps = distance;
			}
			System.out.println(distance);
//			System.out.println("Distance " + distance + "\t Current Slope: " + (distance - prevDistance) + "\t Min Slope: " + minSlope + "\t Min Eps: " + minEps);
			prevDistance = distance;
		}
		System.out.println("End");
		return minEps;
	}

	private TreeMap<Integer, Integer> dbScan(ArrayList<Point> points, double eps, int minPts) {
		TreeMap<Integer, Integer> clusteringSolution = new TreeMap<Integer, Integer>();
		int clusterId = 0;
		ArrayList<Integer> visitedPts = new ArrayList<Integer>();
		for (Point point : points) {
			int geneId = Integer.valueOf(point.getGeneId());
			if (!visitedPts.contains(geneId)) {
				//mark point as visited
				visitedPts.add(geneId);

				// get all points in current point's eps neighborhood
				ArrayList<Point> neighborPts = regionQuery(points, point, eps);
				if (point.getGeneId().equals("1"))
					System.out.println(neighborPts);
				// mark point as noise
				if (neighborPts.size() < minPts) {
					clusteringSolution.put(Integer.valueOf(geneId), -1);
				} else {
					clusterId++;
					clusteringSolution.put(Integer.valueOf(geneId), clusterId);
					expandCluster(clusteringSolution, visitedPts, points, neighborPts, clusterId, eps, minPts);
				}
			}
		}
		return clusteringSolution;
	}

	private void expandCluster(TreeMap<Integer, Integer> clusteringSolution, ArrayList<Integer> visitedPts, ArrayList<Point> points, ArrayList<Point> neighborPts, int clusterId, double eps, int minPts) {
		for (int i = 0; i < neighborPts.size(); i++) {
			Point point2 = neighborPts.get(i);
			int geneId2 = Integer.valueOf(point2.getGeneId());
			if (!visitedPts.contains(geneId2)) {
				//mark point as visited
				visitedPts.add(geneId2);
				// get all points in current point's eps neighborhood
				ArrayList<Point> neighborPts2 = regionQuery(points, point2, eps);
				if (neighborPts2.size() >= minPts) {
					//					neighborPts.addAll(neighborPts2);
					neighborPts = merge(neighborPts, neighborPts2);
				}
			}
			if (!clusteringSolution.containsKey(geneId2) || clusteringSolution.get(geneId2).intValue() == -1) {
				//				System.out.println("Putting in existing cluster");
				clusteringSolution.put(Integer.valueOf(geneId2), clusterId);
			}
		}
	}

	private ArrayList<Point> merge(final ArrayList<Point> neighborPts, final ArrayList<Point> neighborPts2) {
		for (Point point2 : neighborPts2) {
			if (!neighborPts.contains(point2)) {
				neighborPts.add(point2);
			}
		}
		return neighborPts;
	}

	private ArrayList<Point> regionQuery(ArrayList<Point> points, Point point, double eps) {
		ArrayList<Point> neighborPts = new ArrayList<Point>();
		for (Point point2 : points) {
			if (/*point != point2 && */isInEpsNeighborhood(point, point2, eps)) {
				neighborPts.add(point2);
			}
		}
		return neighborPts;
	}

	private boolean isInEpsNeighborhood(Point point, Point point2, double eps) {
		boolean isNeighbor = false;
		double[] coords = point.getCoords();
		double[] coords2 = point2.getCoords();
		double distance = distanceMeasure.getDistance(coords, coords2);
		if (point.getGeneId().equals("1") && point2.getGeneId().equals("2"))
			System.out.println(distance);
		if (distance <= eps) {
			isNeighbor = true;
		}
		return isNeighbor;
	}
}
