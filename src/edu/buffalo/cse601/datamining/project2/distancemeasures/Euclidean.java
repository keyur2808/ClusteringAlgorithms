package edu.buffalo.cse601.datamining.project2.distancemeasures;

public class Euclidean implements DistanceMeasure {
	
	public double getDistance(double[] ds, double[] ds2) {
		double distance = 0;
		for (int i = 0; i < ds.length; i++) {
			distance += Math.pow(ds[i] - ds2[i], 2);
		}
		return Math.sqrt(distance);
	}

	public float distance(float[] ds, float[] ds2) {
		float distance = 0;
		for (int i = 0; i < ds.length; i++) {
			distance += Math.pow(ds[i] - ds2[i], 2);
		}
		return (float) Math.sqrt(distance);
	}
}
