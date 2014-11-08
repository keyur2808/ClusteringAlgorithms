package edu.buffalo.cse601.datamining.project2.utils;

public class DistanceCalculator {

	private DistanceCalculator() {

	}

	public static double getEuclideanDistance(double x[], double y[], int vectorDimensions) {
		double sum = 0;
		double tmp = 0;
		for (int i = 0; i < vectorDimensions; i++) {
			tmp = Math.pow((x[i] - y[i]), 2);
			sum = sum + tmp;
		}
		return (Math.sqrt(sum));
	}
	
	public static double getEuclideanDistance(double x[], double y[]) {
		double sum = 0;
		double tmp = 0;
		for (int i = 0; i < x.length; i++) {
			tmp = Math.pow((x[i] - y[i]), 2);
			sum = sum + tmp;
		}
		return (Math.sqrt(sum));
	}
}
