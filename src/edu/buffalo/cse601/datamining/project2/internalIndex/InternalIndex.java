package edu.buffalo.cse601.datamining.project2.internalIndex;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.primitives.Doubles;

import edu.buffalo.cse601.datamining.project2.utils.PearsonCorrel;

public class InternalIndex {

	
	double[][]clusterMatrix=null;
	double[][] distanceMatrix=null;
	
	public InternalIndex(double[][]distanceMatrix,Map<Integer,Integer>clusteringSolution) {
		int i = 0;
		this.distanceMatrix=distanceMatrix;
		this.clusterMatrix = new double[clusteringSolution.size()][clusteringSolution.size()];
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

	public double evaluate(){
		double[]clusterArray=Doubles.concat(this.clusterMatrix);
		double[]distanceMatrix=Doubles.concat(this.distanceMatrix);
		PearsonCorrel cor=new PearsonCorrel();
		double val=cor.getCorrelation(clusterArray, distanceMatrix);
		System.out.println(val);
		if(val==Double.NaN){
			return 0.0;
		}else{
			return val;	
		}
	}
	
	
}
