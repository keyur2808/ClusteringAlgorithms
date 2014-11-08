package edu.buffalo.cse601.datamining.project2.loaders;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import edu.buffalo.cse601.datamining.project2.clustering.HierarchicalClustering;
import edu.buffalo.cse601.datamining.project2.datastructures.Cluster;
import edu.buffalo.cse601.datamining.project2.datastructures.DistanceMatrix;
import edu.buffalo.cse601.datamining.project2.datastructures.GeneExpression;
import edu.buffalo.cse601.datamining.project2.externalIndex.JaccardCoefficient;
import edu.buffalo.cse601.datamining.project2.fileparser.LoadDataFile;
import edu.buffalo.cse601.datamining.project2.internalIndex.InternalIndex;
import edu.buffalo.cse601.datamining.project2.plots.PCAPlot;

public class HierarchicalLoader {

	TreeMap<Integer, List<Double>> geneMap = null;
	TreeMap<Integer, Integer> groundTruth = null;
	double[][] distanceMatrix = null;
	int numberOfPartitions = -1;
	HierarchicalClustering clustering = null;

	public List<Cluster> Loader(String fileName, String partitionSize,ArrayList<Double>indexes) {
		try {
			boolean success = false;
			String patient1 = fileName;
			numberOfPartitions = Integer.valueOf(partitionSize);

			if (partitionSize != null && partitionSize != "") {
				numberOfPartitions = Integer.parseInt(partitionSize);
			}

			if (patient1 != null && patient1 != "") {
				LoadDataFile files = new LoadDataFile(patient1);
				success = files.parseFile(1);
				if (success) {

					geneMap = files.getGeneIdExpressionsMap();

					groundTruth = files.getGeneExpressionDefaultCluster();

					DistanceMatrix matrix = new DistanceMatrix();
					distanceMatrix = matrix.generateDistanceMatrix(geneMap);

					List<GeneExpression> exp = files.getGeneExpressions();
					int vectorDim = files.getVectorDim();
					HierarchicalClustering clusterAlgo = new HierarchicalClustering(
							exp, vectorDim, numberOfPartitions);
					Cluster finalCluster = clusterAlgo.buildDendogram();
					List<Cluster> clusters = clusterAlgo.getList();
				
					for (Cluster cluster : clusters) {
						System.out.println(cluster.getExpressions().toString());
					}
					
					JaccardCoefficient coeff=new JaccardCoefficient(groundTruth,clusterAlgo.getClusterMapping());
					indexes.set(0,coeff.evaluate());
					System.out.println(indexes.get(0));
					
					InternalIndex idx=new InternalIndex(distanceMatrix, clusterAlgo.getClusterMapping());
					indexes.set(1,idx.evaluate());
					

					
					
					 //Plots
					Thread plot=new Thread(new PCAPlot(fileName));
					plot.start();
					
					Thread pl=new Thread(new PCAPlot(geneMap,"Hierarchical",clusterAlgo.getClusterMapping()));
					pl.start();
					
					return clusters;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}