package edu.buffalo.cse601.datamining.project2.clustering;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JTextArea;

import com.google.common.primitives.Doubles;




import edu.buffalo.cse601.datamining.project2.datastructures.Cluster;
import edu.buffalo.cse601.datamining.project2.datastructures.GeneExpression;
import edu.buffalo.cse601.datamining.project2.display.MergingOutput;
import edu.buffalo.cse601.datamining.project2.utils.DistanceCalculator;

public class HierarchicalClustering {

	private int minPair1 = -1;
	private int minPair2 = -1;
	private List<GeneExpression> geneExpressionList = null;
	private List<Cluster> clusterList = new ArrayList<>();
	private List<Cluster>clusters=new ArrayList<>();
	private int vectorDimensions = -1;
	private int numberOfPartitions=-1;
	
	public HierarchicalClustering(List<GeneExpression> geneExpression, int vectorDimensions,int numberOfPartitions) {
		this.geneExpressionList = geneExpression;
		this.vectorDimensions = vectorDimensions;
		this.numberOfPartitions=numberOfPartitions;
	}
	
	public List<Cluster> getList(){
		return clusters;
	}

	public Map<Integer,Integer> getClusterMapping(){
		TreeMap<Integer,Integer> map=new TreeMap<>();
		int i=1;
		for(Cluster cluster:clusters){
			for(GeneExpression expression:cluster.getExpressions()){
				map.put(expression.getGeneExpressionId(),i);
			}
			
			i++;
		}
	   
	   return map;
	}
	
	public Cluster buildDendogram() {
		try {
			MergingOutput frame = new MergingOutput();
			frame.setVisible(true);
			JTextArea txt=frame.getTxtrMerge();
			System.out.println("Merging Clusters");
			this.buildInitialClusters();
			while (clusterList.size() != 1) {

				if (numberOfPartitions!=-1 && clusterList.size()==numberOfPartitions){
					clusters.addAll(clusterList);
				}
				
				this.getClosestPair();
				Cluster cl = new Cluster();
				Cluster c1 = clusterList.get(minPair1);
				Cluster c2 = clusterList.get(minPair2);
				cl.add(c1);
				cl.add(c2);
				cl.addAll(c1.getExpressions());
				cl.addAll(c2.getExpressions());
				String output="Merged geneIds "+c1.getExpressions()+" and"+c2.getExpressions();
				System.out.println(output);
				txt.append(output+"\n");
				clusterList.add(cl);
				clusterList.remove(c2);
				clusterList.remove(c1);

			}
			return clusterList.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void buildInitialClusters() {
		for (GeneExpression expression : geneExpressionList) {
			Cluster cluster = new Cluster();
			cluster.add(expression);
			clusterList.add(cluster);
		}
	}

	private void getClosestPair() {
		minPair1 = -1;
		minPair2 = -1;
		double dist = Double.MAX_VALUE, current = 0.0;
		for (int i = 0; i < clusterList.size(); i++) {
			for (int j = 0; j < clusterList.size(); j++) {
				if (i == j)
					continue;
				current = this.getMinDistanceFromCluster(clusterList.get(i), clusterList.get(j));
				if (dist > current) {
					dist = current;
					minPair1 = i;
					minPair2 = j;
				}
			}
		}
	}

	private double getMinDistanceFromCluster(Cluster cluster1, Cluster cluster2) {
		double distance = Double.MAX_VALUE;
		double tmp = 0.0;
		ArrayList<GeneExpression> expressionList1 = cluster1.getExpressions();
		ArrayList<GeneExpression> expressionList2 = cluster2.getExpressions();
		for (int i = 0; i < expressionList1.size(); i++) {
			for (int j = 0; j < expressionList2.size(); j++) {

				double[] x = Doubles.toArray(expressionList1.get(i).getExpressionValues());
				double[] y = Doubles.toArray(expressionList2.get(j).getExpressionValues());
				tmp = DistanceCalculator.getEuclideanDistance(x, y, vectorDimensions);
				if (distance > tmp) {
					distance = tmp;
				}
			}
		}

		return distance;
	}

	// List<Integer>geneIdList=new ArrayList<>(geneExpressionListMap.keySet());
	// for (int i=0;i<geneIdList.size();i++){
	// for (int j=0;j<geneIdList.size();j++){
	// if (i==j)continue;
	// List<Double>a=geneExpressionListMap.get(geneIdList.get(i));
	// List<Double>b=geneExpressionListMap.get(geneIdList.get(j));
	// double
	// distance=getEuclideanDistance(Doubles.toArray(a),Doubles.toArray(b));
	// }
	// }

}
