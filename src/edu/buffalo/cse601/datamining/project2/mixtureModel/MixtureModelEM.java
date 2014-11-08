package edu.buffalo.cse601.datamining.project2.mixtureModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.MixtureMultivariateNormalDistribution;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.fitting.MultivariateNormalMixtureExpectationMaximization;
import org.apache.commons.math3.util.Pair;

import edu.buffalo.cse601.datamining.project2.clustering.KMeans;
import edu.buffalo.cse601.datamining.project2.datastructures.DistanceMatrix;
import edu.buffalo.cse601.datamining.project2.externalIndex.JaccardCoefficient;
import edu.buffalo.cse601.datamining.project2.externalIndex.RandIndex;
import edu.buffalo.cse601.datamining.project2.inputReader.ReadIn;
import edu.buffalo.cse601.datamining.project2.internalIndex.InternalIndex;
import edu.buffalo.cse601.datamining.project2.plots.PCAPlot;
import edu.buffalo.cse601.datamining.project2.pojo.Point;
import edu.buffalo.cse601.datamining.project2.utils.GetGeneMap;
import edu.buffalo.cse601.datamining.project2.utils.Normalizer;
import edu.buffalo.cse601.datamining.project2.utils.PropertyReader;

public class MixtureModelEM {

	private TreeMap<Integer, Integer> groundTruth = new TreeMap<Integer, Integer>();
	private double THRESHOLD = 0;
	private double ENTROPYTHRESHOLD = 0;

	// private double PROBTHRESHOLD;

	public TreeMap<Integer, Integer> main(String filename, int numClusters, double threshold, ArrayList<Double> indexes) {
		// MixtureModelEM mixtureModelEM = new MixtureModelEM();
		this.THRESHOLD = threshold;
		// mixtureModelEM.PROBTHRESHOLD = Double.parseDouble((String)
		// properties.get("PROBTHRESHOLD"));

		String filePath = filename;

		ArrayList<Point> points = new ArrayList<Point>();

		this.groundTruth = ReadIn.readIn(filePath, points);
		TreeMap<Integer, List<Double>> geneMap = GetGeneMap.getGeneMap(points);
		int numDimensions = points.get(0).getCoords().length;

		System.out.println(points.size());
		System.out.println(filename);

		TreeMap<Integer, Integer> clusteringSolution = EM(points, numClusters, numDimensions, geneMap);
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

		for (Integer cluster : clusters.keySet()) {
			System.out.println("Cluster Id: " + cluster + " \t Cluster size: " + clusters.get(cluster).size());
		}

		double[][] data = new double[points.size()][numDimensions];
		for (int i = 0; i < points.size(); i++) {
			data[i] = points.get(i).getCoords();
		}

		DistanceMatrix matrix = new DistanceMatrix();
		double[][] distanceMatrix = matrix.generateDistanceMatrix(geneMap);

		InternalIndex idx = new InternalIndex(distanceMatrix, clusteringSolution);
		indexes.set(1, idx.evaluate());

		System.out.println(clusters);

		Thread pl = new Thread(new PCAPlot(geneMap, "MixtureModel", clusteringSolution));
		pl.start();

		return clusteringSolution;
	}

	public static void main(String[] args) {
		MixtureModelEM mixtureModelEM = new MixtureModelEM();
		Properties properties = PropertyReader.getProperties("MixtureModel.properties");
		String filename = (String) properties.get("FILENAME");
		int numClusters = Integer.parseInt((String) properties.get("NUMCLUSTERS"));
		if (properties.get("THRESHOLD") != null)
			mixtureModelEM.THRESHOLD = Double.parseDouble((String) properties.get("THRESHOLD"));
		if (properties.get("ENTROPYTHRESHOLD") != null)
			mixtureModelEM.ENTROPYTHRESHOLD = Double.parseDouble((String) properties.get("ENTROPYTHRESHOLD"));
		// mixtureModelEM.PROBTHRESHOLD = Double.parseDouble((String)
		// properties.get("PROBTHRESHOLD"));

		String filePath = "Input/" + filename;

		ArrayList<Point> points = new ArrayList<Point>();

		mixtureModelEM.groundTruth = ReadIn.readIn(filePath, points);

		int numDimensions = points.get(0).getCoords().length;
		// Normalize
		points = Normalizer.normalize(points, numDimensions);

		TreeMap<Integer, List<Double>> geneMap = GetGeneMap.getGeneMap(points);

		System.out.println(points.size());
		System.out.println(filename);

		TreeMap<Integer, Integer> clusteringSolution = mixtureModelEM.EM(points, numClusters, numDimensions, geneMap);
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

			System.out.println("Gene Id: " + geneId + "\t Cluster Id from solution: " + clusterId + "\t Ground truth cluster: " + mixtureModelEM.groundTruth.get(geneId));
		}

		JaccardCoefficient jc = new JaccardCoefficient(mixtureModelEM.groundTruth, clusteringSolution);
		double jaccardCoeff = jc.evaluate();

		System.out.println("Jaccard Coefficient: " + jaccardCoeff);

		RandIndex ri = new RandIndex(mixtureModelEM.groundTruth, clusteringSolution);
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
		PCAPlot pl = new PCAPlot(geneMap, "EM", clusteringSolution);
		pl.run();
		/*
		 * double[][] data = new double[points.size()][numDimensions];
		 * 
		 * for (int i = 0; i < points.size(); i++) { data[i] =
		 * points.get(i).getCoords(); } System.out.println("Num Points: " +
		 * points.size() + "Num Dimensions: " + numDimensions); data =
		 * Normalizer.normalize(data, numDimensions);
		 * 
		 * MultivariateNormalMixtureExpectationMaximization mnmem = new
		 * MultivariateNormalMixtureExpectationMaximization(data);
		 * MixtureMultivariateNormalDistribution mmndInit =
		 * MultivariateNormalMixtureExpectationMaximization.estimate(data,
		 * numClusters); mnmem.fit(mmndInit);
		 * MixtureMultivariateNormalDistribution mmndFinal =
		 * mnmem.getFittedModel(); List<Pair<Double,
		 * MultivariateNormalDistribution>> components =
		 * mmndFinal.getComponents(); HashMap<String, Integer>
		 * idealClusteringSolution = new HashMap<String, Integer>();
		 * 
		 * for (int i = 0; i < points.size(); i++) { double max =
		 * Double.NEGATIVE_INFINITY; int clusterId = -1; for (int k = 0; k <
		 * numClusters; k++) { Pair<Double, MultivariateNormalDistribution>
		 * component = components.get(k); double Pik = component.getKey();
		 * MultivariateNormalDistribution distribution = component.getValue();
		 * double prob = distribution.density(points.get(i).getCoords()); double
		 * netProb = Pik * prob; if (netProb > max) { max = netProb; clusterId =
		 * k; } } idealClusteringSolution.put(points.get(i).getGeneId(),
		 * clusterId); } System.out.println("Ideal Log Likelihood: " +
		 * mnmem.getLogLikelihood());
		 * 
		 * // jc = new JaccardCoefficient(mixtureModelEM.groundTruth,
		 * idealClusteringSolution); // jaccardCoeff = jc.evaluate();
		 * 
		 * System.out.println("Ideal Jaccard Coefficient: " + jaccardCoeff);
		 * 
		 * // ri = new RandIndex(mixtureModelEM.groundTruth,
		 * idealClusteringSolution); // randIndex = ri.evaluate();
		 * 
		 * System.out.println("Ideal Rand Index: " + randIndex);
		 */
	}

	private TreeMap<Integer, Integer> EM(ArrayList<Point> points, int numClusters, int numDimensions, TreeMap<Integer, List<Double>> geneMap) {
		TreeMap<Integer, Integer> clusteringSolution = new TreeMap<Integer, Integer>();
		double[] pi = new double[numClusters];
		double[][] mu = new double[numClusters][numDimensions];
		double[][] covariance = new double[numClusters][numDimensions];

		double[][] r = new double[points.size()][numClusters];

		double prevLogLikelihood = -Double.MAX_VALUE;

		// initialize(pi, mu, covariance, numClusters, numDimensions);

		kMeansInitialize(points, geneMap, pi, mu, covariance, numClusters, numDimensions);

		double[] maxR = new double[numClusters];
		Arrays.fill(maxR, (1.0 / numClusters));
		double maxEntropy = getEntropy(maxR);

		while (true) {
			// E - Step
			for (int i = 0; i < points.size(); i++) {
				double runningSum = 0;
				for (int k = 0; k < numClusters; k++) {
					r[i][k] = EstepNumerator(points.get(i), pi[k], mu[k], covariance[k]);
					// System.out.println("r[" + i + "][" + k + "] = " +
					// r[i][k]);
					runningSum += r[i][k];
				}
				for (int k = 0; k < numClusters; k++) {
					// System.out.println("New rik: " + r[i][k] + "/" +
					// runningSum + " = " + r[i][k] / runningSum);
					r[i][k] /= runningSum;
				}
			}

			double newLogLikehood = computeLogLikelihood(points, r, pi, mu, covariance);

			// System.out.println("Prev Log Likelihood: " + prevLogLikelihood +
			// "\t Log Likelihood: " + newLogLikehood + " \t Diff: " +
			// (newLogLikehood - prevLogLikelihood) + " THRESHOLD: " +
			// THRESHOLD);

			if (newLogLikehood - prevLogLikelihood >= THRESHOLD) {
				prevLogLikelihood = newLogLikehood;
			} else {
				System.out.println("Log Likelihood: " + newLogLikehood);
				break;
			}

			// M-Step
			double sumRik[] = new double[pi.length];
			for (int k = 0; k < pi.length; k++) {
				for (int i = 0; i < points.size(); i++) {
					sumRik[k] += r[i][k];
				}
				pi[k] = sumRik[k] / points.size();
			}
			for (int k = 0; k < pi.length; k++) {
				for (int j = 0; j < numDimensions; j++) {
					double sumRikXij = 0;
					double sumRikXijMukj = 0;
					for (int i = 0; i < points.size(); i++) {
						sumRikXij += r[i][k] * points.get(i).getCoords()[j];
						sumRikXijMukj += r[i][k] * Math.pow(points.get(i).getCoords()[j] - mu[k][j], 2);
					}
					mu[k][j] = sumRikXij / sumRik[k];
					covariance[k][j] = sumRikXijMukj / sumRik[k];
				}
			}
		}

		for (int i = 0; i < points.size(); i++) {
			double max = Double.NEGATIVE_INFINITY;
			int clusterId = -1;
			// double totalProbDensity = 0;
			System.out.print("Gene Id: " + points.get(i).getGeneId() + " \t Probabilities: ");
			double entropy = getEntropy(r[i]);

			if (maxEntropy - entropy >= ENTROPYTHRESHOLD) {
				for (int k = 0; k < numClusters; k++) {
					/*
					 * if (k == 0) { totalProbDensity = pi[k] *
					 * probDensity(points.get(i), mu[k], covariance[k]) /
					 * r[i][k]; if (totalProbDensity < PROBTHRESHOLD) { break; }
					 * }
					 */
					System.out.print(r[i][k] + "\t");
					if (r[i][k] > max) {
						max = r[i][k];
						clusterId = k;
					}
				}
				System.out.println();
			} else{
				clusterId = -1;
				System.out.println();
			}
			clusteringSolution.put(Integer.valueOf(points.get(i).getGeneId()), clusterId);
		}
		return clusteringSolution;

	}

	private void kMeansInitialize(ArrayList<Point> points, TreeMap<Integer, List<Double>> geneMap, double[] pi, double[][] mu, double[][] covariance, int numClusters, int numDimensions) {
		KMeans kMeans = new KMeans(geneMap, null, numClusters);
		Map<Integer, Integer> kMeansClusters = kMeans.evaluateClusters();
		Map<Integer, double[]> clusters = new HashMap<Integer, double[]>();
		Map<Integer, ArrayList<Point>> clusterPoints = new HashMap<Integer, ArrayList<Point>>();
		Map<Integer, Integer> clusterMembership = new HashMap<Integer, Integer>();
		for (Entry<Integer, Integer> entry : kMeansClusters.entrySet()) {
			int geneId = entry.getKey();
			int clusterId = entry.getValue();
			double[] sums = null;
			int memberCount = 0;
			ArrayList<Point> clusterPointList = null;
			if (clusters.containsKey(clusterId)) {
				sums = clusters.get(clusterId);
				memberCount = clusterMembership.get(clusterId);
				clusterPointList = clusterPoints.get(clusterId);
			} else {
				sums = new double[numDimensions];
				clusterPointList = new ArrayList<Point>();
			}
			List<Double> pointList = geneMap.get(geneId);
			double[] coords = new double[pointList.size()];
			for (int i = 0; i < pointList.size(); i++) {
				Double coord = pointList.get(i);
				coords[i] = coord;
				sums[i] += coord;
			}
			clusters.put(clusterId, sums);
			memberCount++;
			clusterMembership.put(clusterId, memberCount);

			Point point = new Point();
			point.setGeneId("" + geneId);
			point.setCoords(coords);
			clusterPointList.add(point);
			clusterPoints.put(clusterId, clusterPointList);
		}

		int count = 0;
		for (Entry<Integer, double[]> entry : clusters.entrySet()) {
			double[] sums = entry.getValue();
			int memberCount = clusterMembership.get(entry.getKey());
			double[] means = new double[sums.length];
			for (int i = 0; i < sums.length; i++) {
				means[i] = sums[i] / memberCount;
			}
			mu[count] = means;
			pi[count] = memberCount / (double) geneMap.size();
			covariance[count] = getVariance(mu[count], clusterPoints.get(entry.getKey()));
			System.out.println("Cluster Id: " + count + " \t Cluster size: " + memberCount);
			count++;
		}
	}

	private double[] getVariance(double[] mean, List<Point> clusterPoints) {
		double[] temp = new double[mean.length];
		for (int j = 0; j < mean.length; j++) {
			for (Point a : clusterPoints) {
				temp[j] += (mean[j] - a.getCoords()[j]) * (mean[j] - a.getCoords()[j]);
			}
			temp[j] /= (clusterPoints.size() - 1);
		}
		return temp;
	}

	private double getEntropy(double[] d) {
		double sum = 0;
		for (int i = 0; i < d.length; i++) {
			sum += (d[i] * Math.log(d[i]) / Math.log(2));
		}
		return -sum;
	}

	/*
	 * private void kMeansPlusPlusInitialize(ArrayList<Point> points, double[]
	 * pi, double[][] mu, double[][] covariance, int numClusters, int
	 * numDimensions) { KMeansPlusPlusClusterer<DoublePoint> kmppc = new
	 * KMeansPlusPlusClusterer<DoublePoint>(numClusters, 50);
	 * ArrayList<DoublePoint> doublePoints = new ArrayList<DoublePoint>(); for
	 * (Point point : points) { doublePoints.add(new
	 * DoublePoint(point.getCoords())); } List<CentroidCluster<DoublePoint>>
	 * listofClusters = kmppc.cluster(doublePoints); int count = 0; for
	 * (CentroidCluster<DoublePoint> cluster : listofClusters) { mu[count] =
	 * cluster.getCenter().getPoint(); List<DoublePoint> clusterPoints =
	 * cluster.getPoints(); pi[count] = (double) clusterPoints.size() / (double)
	 * points.size(); covariance[count] = getVariance(mu[count], clusterPoints);
	 * System.out.println("Cluster Id: " + count + " \t Cluster size: " +
	 * cluster.getPoints().size()); count++; } }
	 */

	/*
	 * private void initialize(double[] pi, double[][] mu, double[][]
	 * covariance, int numClusters, int numDimensions) { // initialize pi
	 * Arrays.fill(pi, 1.0 / (double) numClusters);
	 * 
	 * // initialize mu for (int k = 0; k < numClusters; k++) { for (int j = 0;
	 * j < numDimensions; j++) { double randomFactor = Math.random() * 40;
	 * mu[k][j] = Math.random() * randomFactor; } } //initialize covariance for
	 * (int k = 0; k < numClusters; k++) { for (int j = 0; j < numDimensions;
	 * j++) { double randomFactor = Math.random() * 4; covariance[k][j] =
	 * Math.random() * randomFactor; } } }
	 */
	private double computeLogLikelihood(ArrayList<Point> points, double[][] r, double[] pi, double[][] mu, double[][] covariance) {
		double logLikehood = 0;
		for (int i = 0; i < points.size(); i++) {
			for (int k = 0; k < pi.length; k++) {
				double logPik = Math.log(pi[k]);
				double prob = probDensity(points.get(i), mu[k], covariance[k]);
				double logProb = Math.log(prob);
				/*
				 * if (prob == 0) { logProb = 0; }
				 */
				double prod = r[i][k] * (logPik + logProb);
				// System.out.println("logPik: " + logPik + "\t prob: " + prob +
				// "\t logProb: " + logProb + "\t r[" + i + "][" + k + "]: " +
				// r[i][k] + "\t prod: " + prod);
				logLikehood += prod;
			}
		}
		return logLikehood;
	}

	private double EstepNumerator(Point point, double pi, double[] mu, double[] covariance) {
		double prob = probDensity(point, mu, covariance);
		// System.out.println("pi : " + pi + " \t prob: " + prob);
		return pi * prob;
	}

	private double probDensity(Point point, double[] mu, double[] covariance) {
		int dim = mu.length;
		double determinant = getDeterminant(covariance);
		double exponent = getExponentTerm(point, mu, covariance);
		// System.out.println("Exponent: " + exponent);
		double prob = Math.pow(2 * Math.PI, -0.5 * dim) * Math.pow(determinant, -0.5) * exponent;
		/*
		 * System.out.println("Point: " + point.toString());
		 * System.out.print("Mu: "); for (int k = 0; k < mu.length; k++) {
		 * System.out.print(mu[k]); System.out.print(","); }
		 * System.out.println(); System.out.print("Covariance: "); for (int k =
		 * 0; k < covariance.length; k++) { System.out.print(covariance[k]);
		 * System.out.print(","); } System.out.println();
		 */
		// System.out.println("determinant: " + determinant + "\t" + "\tprob: "
		// + prob);
		/*
		 * if(prob > 1){ System.out.println("prob > 0");
		 * System.out.println("Point: " + point.toString());
		 * System.out.print("Mu: "); for (int k = 0; k < mu.length; k++) {
		 * System.out.print(mu[k]); System.out.print(","); }
		 * System.out.println(); System.out.print("Covariance: "); for (int k =
		 * 0; k < covariance.length; k++) { System.out.print(covariance[k]);
		 * System.out.print(","); } System.out.println(); System.exit(1); }
		 */
		return prob;
	}

	private double getExponentTerm(Point point, double[] mu, double[] covariance) {
		double[] values = point.getCoords();
		final double[] centered = new double[values.length];
		for (int i = 0; i < centered.length; i++) {
			centered[i] = values[i] - mu[i];
		}
		double sum = 0;
		for (int i = 0; i < covariance.length; i++) {
			sum += centered[i] * centered[i] / covariance[i];
		}
		return Math.exp(-0.5 * sum);
	}

	public double getDeterminant(double[] matrix) {
		double prod = 1;
		for (double element : matrix) {
			prod *= element;
		}
		return prod;
	}
}