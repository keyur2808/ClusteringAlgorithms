package edu.buffalo.cse601.datamining.project2.utils;

import com.google.common.math.DoubleMath;

public class PearsonCorrel {

	public double getCorrelation(double[] x, double[] y) {

		double xMean = 0;
		double yMean = 0;

		for (double xValue : x) {
			xMean += xValue;
		}
		xMean = xMean / x.length;

		for (double yValue : y) {
			yMean += yValue;
		}
		yMean = yMean / y.length;

		double[] xdiff = new double[x.length];
		double[] ydiff = new double[y.length];
		double[] xdiff2 = new double[x.length];
		double[] ydiff2 = new double[y.length];

		double sum = 0, prod = 1;
		for (int i = 0; i < x.length; i++) {
			xdiff[i] = x[i] - xMean;
			ydiff[i] = y[i] - yMean;
			prod = xdiff[i] * ydiff[i];
			xdiff2[i] = Math.pow(xdiff[i], 2);
			ydiff2[i] = Math.pow(ydiff[i], 2);
			sum += prod;
		}

		double xSqSum = Math.sqrt(DoubleMath.mean(xdiff2) * x.length);
		double ySqSum = Math.sqrt(DoubleMath.mean(ydiff2) * y.length);

		double corr = (sum / (xSqSum * ySqSum));

		return corr;
	}

}
