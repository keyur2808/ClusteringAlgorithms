package edu.buffalo.cse601.datamining.project2.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import com.google.common.primitives.Doubles;

import edu.buffalo.cse601.datamining.project2.utils.DistanceCalculator;



public class KMeans {

    private TreeMap<Integer, List<Double>> geneExpressionListMap;
    private int numberOfPartitions = -1;
    private int vectorDimensions = -1;
    private TreeMap<Integer, ArrayList<Integer>> geneCluster = new TreeMap<Integer, ArrayList<Integer>>();
    public TreeMap<Integer, ArrayList<Integer>> getGeneCluster() {
		return geneCluster;
	}

	private double[][]initialCentroids=null;
    private double SSError=-1;
    private int iterations=-1;

    public KMeans(TreeMap<Integer, List<Double>> geneMap, ArrayList<Double> initialScale, int numberOfPartitions) {
    	this.geneExpressionListMap = geneMap;
        this.numberOfPartitions = numberOfPartitions;
        this.vectorDimensions = geneExpressionListMap.get(1).size();
        //this.initialScale = initialScale;
        for (int a = 0; a < numberOfPartitions; a++) {  //Ashish
            ArrayList<Integer> emptyList = new ArrayList<Integer>();
            geneCluster.put(a, emptyList);
        }
    }
    
    public KMeans(TreeMap<Integer, List<Double>> geneMap, ArrayList<Double> initialScale, int numberOfPartitions,double [][]initialCentroids,double SSError,int iterations){
    	this.geneExpressionListMap = geneMap;
        this.numberOfPartitions = numberOfPartitions;
        this.vectorDimensions = geneExpressionListMap.get(1).size();
        this.initialCentroids = initialCentroids;
        this.SSError=SSError;
        this.iterations=iterations;
        for (int a = 0; a < numberOfPartitions; a++) {  //Ashish
            ArrayList<Integer> emptyList = new ArrayList<Integer>();
            geneCluster.put(a, emptyList);
        }

    }
    
    public KMeans(TreeMap<Integer, List<Double>> geneMap, ArrayList<Double> initialScale, int numberOfPartitions,double [][]initialCentroids) {
    	this.geneExpressionListMap = geneMap;
        this.numberOfPartitions = numberOfPartitions;
        this.vectorDimensions = geneExpressionListMap.get(1).size();
        this.initialCentroids = initialCentroids;
        for (int a = 0; a < numberOfPartitions; a++) {  //Ashish
            ArrayList<Integer> emptyList = new ArrayList<Integer>();
            geneCluster.put(a, emptyList);
        }
    }

    /*private double[][] generateInitialMeanVectors() {
    double[][] centroids = new double[numberOfPartitions][vectorDimensions];
    double[] initialVectors = Doubles.toArray(initialScale);
    for (int i = 0; i < numberOfPartitions; i++) {
    for (int j = 0; j < initialVectors.length; j++) {
    centroids[i][j] = initialVectors[j] * Math.random() / numberOfPartitions;
    System.out.print(centroids[i][j] + "  ");
    }
    System.out.println();
    System.out.println();
    }
    return centroids;
    }*/
    
    
    private double[][] generateInitialMeanVectors() {
        double[][] centroids = new double[numberOfPartitions][vectorDimensions];
        Random random = new Random();
        for (int i = 0; i < numberOfPartitions; i++) {
            int randomNumber = random.nextInt(geneExpressionListMap.size());
            System.out.println("Random Number: " + randomNumber + " \t " + geneExpressionListMap.get(randomNumber));
            System.out.println();
            centroids[i] = Doubles.toArray(geneExpressionListMap.get(randomNumber));
        }
        return centroids;
    }

    public Map<Integer, Integer> evaluateClusters() {
    	double[][] centroids=null;
    	if (initialCentroids==null || initialCentroids.length<numberOfPartitions){
    		  centroids = generateInitialMeanVectors();
        }else{
        	centroids=initialCentroids;
        }
    	
        double min = Double.MAX_VALUE;
        double distance = Double.MAX_VALUE;
        int clusterId = 0;
        double currentError = 0;
        double previousError = Double.MAX_VALUE;
        int count = 0;
        Map<Integer,Integer>outputClustersMapping=new HashMap<>();

        while ( (!isConverged(previousError,currentError)) && (count<iterations||iterations==-1)) {
            for (int i = 0; i < numberOfPartitions; i++) {
                ArrayList<Integer> geneIds = geneCluster.get(i);
                geneIds.clear();
            }
            for (Entry<Integer, List<Double>> entry : geneExpressionListMap.entrySet()) {
                min = Double.MAX_VALUE;
                double[] vector = Doubles.toArray(entry.getValue());
                for (int k = 0; k < numberOfPartitions; k++) {
                    distance = DistanceCalculator.getEuclideanDistance(vector, centroids[k],vectorDimensions);
                    //distance = EuclideanDistanceARM.distance(vector, centroids[k]);
                    if (distance < min) {
                        min = distance;
                        clusterId = k;
                    }
                }
                ArrayList<Integer> geneIds = geneCluster.get(clusterId);
                geneIds.add(entry.getKey());
                //geneCluster.put(clusterId, geneIds);  //Ashish
            }
            previousError = currentError;
            currentError = getSSE(centroids, geneCluster);
            centroids = updateCentroids();
            count++;
//            System.out.println("Count: " + count + " \t Previous Error: " + previousError + " \t Current Error: " + currentError);
            //			if(count == 1)
//            System.out.println(geneCluster);
        }
        //		System.out.println(count);
        for (Entry<Integer,ArrayList<Integer>>entry :geneCluster.entrySet())
        {
        	for(Integer id: entry.getValue()){
              outputClustersMapping.put(id, entry.getKey());     		
        }
        }
        return outputClustersMapping;
    }
    
    private boolean isConverged(double previousError,double currentError){
    	if(SSError!=-1){
    		return (!(Math.abs(previousError - currentError) > SSError));
    	}else{
    		return (!(Math.abs(previousError - currentError) > 0));
    	}
    }

    private double getSSE(double[][] centroids, TreeMap<Integer, ArrayList<Integer>> geneCluster) {
        double sse = 0;
        ArrayList<Integer> tempAL = null;
        //Euclidean ec = new Euclidean();
        for (int i = 0; i < numberOfPartitions; i++) {
            tempAL = geneCluster.get(i);
            //sum = 0;
            for (int j = 0; j < tempAL.size(); j++) {
                List<Double> x = geneExpressionListMap.get(tempAL.get(j));
                double[] expressions = Doubles.toArray(x);
                //				sum = sum + DistanceCalculator.getEuclideanDistance(expressions, centroids[i], vectorDimensions);
                sse += DistanceCalculator.getEuclideanDistance(expressions, centroids[i],vectorDimensions);
                //sse += EuclideanDistanceARM.distance(expressions, centroids[i]);
            }
            //sse += sum;
        }

        return sse;
    }

    private double[][] updateCentroids() {
        double[][] centroids = new double[numberOfPartitions][vectorDimensions];
        System.out.println("Updated Centroids");
        for (int i = 0; i < numberOfPartitions; i++) {
        	System.out.println("Cluster id: " + i);
            ArrayList<Integer> geneExpressions = geneCluster.get(i);
            //System.out.println("geneExpressions: " + geneExpressions);
            System.out.println("geneExpressions.size(): " + geneExpressions.size());
            double[] tmp = new double[vectorDimensions];
            for (Integer genId : geneExpressions) {
                List<Double> values = geneExpressionListMap.get(genId);
                for (int j = 0; j < values.size(); j++) {
                    tmp[j] += (values.get(j) / geneExpressions.size()); //Ashish
                }
            }
            centroids[i] = tmp;
            for (int j = 0; j < centroids[i].length; j++) {
                System.out.print(centroids[i][j] + " ");
            }
            System.out.println();
        }
        return centroids;
    }
    
  }
