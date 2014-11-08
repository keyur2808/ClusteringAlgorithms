package edu.buffalo.cse601.datamining.project2.utils;

public class Euclidean implements DistanceMeasure {
	
	public double getDistance(double[] ds, double[] ds2) {
		double distance = 0;
		for (int i = 0; i < ds.length; i++) {
			distance += Math.pow(ds[i] - ds2[i], 2);
		}
		return Math.sqrt(distance);
	}
}
