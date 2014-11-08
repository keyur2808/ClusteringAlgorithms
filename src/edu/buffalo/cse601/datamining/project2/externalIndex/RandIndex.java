package edu.buffalo.cse601.datamining.project2.externalIndex;



import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class RandIndex {

	private int[][] clusterMatrix;
	private int[][] groundTruthMatrix;

	public RandIndex(TreeMap<Integer, Integer> groundTruth, TreeMap<Integer, Integer> clusteringSolution) {
		groundTruthMatrix = new int[groundTruth.size()][groundTruth.size()];
		clusterMatrix = new int[clusteringSolution.size()][clusteringSolution.size()];

		int i = 0;
		for (Entry<Integer, Integer> entry1 : groundTruth.entrySet()) {
			int clusterId1 = entry1.getValue();
			int j = 0;
			for (Entry<Integer, Integer> entry2 : groundTruth.entrySet()) {
				int clusterId2 = entry2.getValue();
				if ((clusterId1 != -1 && clusterId2 != -1) && (clusterId1 == clusterId2)) {
					groundTruthMatrix[i][j] = 1;
					groundTruthMatrix[j][i] = 1;
				} else {
					groundTruthMatrix[i][j] = 0;
					groundTruthMatrix[j][i] = 0;
				}
				j++;
			}
			i++;
		}

		i = 0;
		for (Entry<Integer, Integer> entry1 : clusteringSolution.entrySet()) {
			int clusterId1 = entry1.getValue();
			int j = 0;
			for (Entry<Integer, Integer> entry2 : clusteringSolution.entrySet()) {
				int clusterId2 = entry2.getValue();
				if ((clusterId1 != -1 && clusterId2 != -1) && (clusterId1 == clusterId2)) {
					clusterMatrix[i][j] = 1;
					clusterMatrix[j][i] = 1;
				} else {
					clusterMatrix[i][j] = 0;
					clusterMatrix[j][i] = 0;
				}
				j++;
			}
			i++;
		}
	}

	public double evaluate() {
		int M11 = 0;
		int M10 = 0;
		int M00 = 0;
		for (int i = 0; i < clusterMatrix.length; i++) {
			for (int j = 0; j < clusterMatrix.length; j++) {
				if (clusterMatrix[i][j] == 1 && groundTruthMatrix[i][j] == 1) {
					M11++;
				} else if (clusterMatrix[i][j] == 0 && groundTruthMatrix[i][j] == 0) {
					M00++;
				} else {
					M10++;
				}
			}
		}
		return (double) (M11 + M00) / (double) (M11 + M10 + M00);
	}

}
