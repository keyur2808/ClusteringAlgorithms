package edu.buffalo.cse601.datamining.project2.datastructures;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.primitives.Doubles;

import edu.buffalo.cse601.datamining.project2.distancemeasures.Euclidean;
import edu.buffalo.cse601.datamining.project2.utils.DistanceCalculator;

public class DistanceMatrix {

	public double[][] generateDistanceMatrix(TreeMap<Integer, List<Double>> dataPoints) {
		double[][] distanceMatrix = new double[dataPoints.size()][dataPoints.size()];
		Euclidean dist = new Euclidean();
		int i = 0, j = 0;
		for (Map.Entry<Integer, List<Double>> entry : dataPoints.entrySet()) {
			j = 0;
			for (Map.Entry<Integer, List<Double>> entry2 : dataPoints.entrySet()) {
				if (entry.getKey().equals(entry2.getKey())) {
					//System.out.println(entry.getKey()+" "+entry2.getKey());
					distanceMatrix[i][j]=0.0;
					//continue;
				} else {
					if (entry.getValue() != null && entry2.getValue() != null) {
						distanceMatrix[i][j] = dist.getDistance(Doubles.toArray(entry.getValue()), Doubles.toArray(entry2.getValue()));
					}
				}
				j++;
			}
			i++;
		}
		return distanceMatrix;
	}

}
