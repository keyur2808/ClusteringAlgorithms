package edu.buffalo.cse601.datamining.project2.loaders;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import edu.buffalo.cse601.datamining.project2.clustering.*;
import edu.buffalo.cse601.datamining.project2.datastructures.*;
import edu.buffalo.cse601.datamining.project2.externalIndex.JaccardCoefficient;
import edu.buffalo.cse601.datamining.project2.fileparser.InitialCentroids;
import edu.buffalo.cse601.datamining.project2.fileparser.LoadDataFile;
import edu.buffalo.cse601.datamining.project2.internalIndex.InternalIndex;
import edu.buffalo.cse601.datamining.project2.plots.PCAPlot;

public class KMeansLoader {
	//means = new KMeans(geneMap, initVectors,
	//numberOfPartitions, initialMeanVectors);
	TreeMap<Integer, List<Double>> geneMap=null;
	TreeMap<Integer,Integer>groundTruth=null;
	double[][] distanceMatrix=null;
	int numberOfPartitions = -1;
	int iterations = -1;
	double SSError = -1;
	KMeans means = null;
	
	public Map<Integer,ArrayList<Integer>> KMeansAlgo(String fileName,String rowIds[],String partitionSize,String iteration,String SSE,ArrayList<Double>indexes) {
		try {
			boolean success = false;
			String patient1 = fileName;
			numberOfPartitions=Integer.valueOf(partitionSize);
			ArrayList<Integer> rowsIdOfCentroids = new ArrayList<>();
			InitialCentroids centroids = null;
			double initialMeanVectors[][] = null;
			
			if (SSE!=null && !SSE.equals("")){
				SSError=Double.valueOf(SSE);
				
			}
		
			if (iteration!=null && !iteration.equals("")){
				iterations=Integer.valueOf(iteration);
			}
			
			if (rowIds!=null){
			for (String row : rowIds) {
				rowsIdOfCentroids.add(Integer.valueOf(row));
			}
			}
			
			if (partitionSize != null && partitionSize != "") {
				numberOfPartitions = Integer.parseInt(partitionSize);
			}

			if (patient1 != null && patient1 != "") {
				LoadDataFile files = new LoadDataFile(patient1);
				success = files.parseFile(0);
				if (success) {

					geneMap = files
							.getGeneIdExpressionsMap();
					
					groundTruth=files.getGeneExpressionDefaultCluster();

					DistanceMatrix matrix=new DistanceMatrix();
					distanceMatrix=matrix.generateDistanceMatrix(geneMap);
					
					if (initialMeanVectors == null) {
						centroids = new InitialCentroids();
						initialMeanVectors = centroids.loadCentroids(rowsIdOfCentroids,
								geneMap);
					}
					
					means = new KMeans(geneMap, null,
									numberOfPartitions, initialMeanVectors,SSError,iterations);
						
							
					//SSError,iterations
					Map<Integer,Integer>kMeansClusters=means.evaluateClusters();
					
					
					JaccardCoefficient coeff=new JaccardCoefficient(groundTruth,kMeansClusters);
					indexes.set(0,coeff.evaluate());
					System.out.println(indexes.get(0));
					
					InternalIndex idx=new InternalIndex(distanceMatrix, kMeansClusters);
					indexes.set(1,idx.evaluate());
					

					// Plots
					Thread t=new Thread(new PCAPlot(fileName));
					t.start();
					
					Thread t1=new Thread(new PCAPlot(geneMap,"Kmeans",kMeansClusters));
					t1.start();
					//t.start();
					return means.getGeneCluster();
					
					
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
