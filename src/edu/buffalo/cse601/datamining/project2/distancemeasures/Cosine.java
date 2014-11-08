package edu.buffalo.cse601.datamining.project2.distancemeasures;


public class Cosine  implements DistanceMeasure {
	public double getDistance(double[] ds, double[] ds2) {
		double prod = 0;
		double mod1 = 0;
		double mod2 = 0;
		for (int i = 0; i < ds.length; i++) {
			prod += ds[i] * ds2[i];
			mod1 += Math.pow(ds[i], 2);
			mod2 += Math.pow(ds2[i], 2);
		}
		mod1 = Math.sqrt(mod1);
		mod2 = Math.sqrt(mod2);
		prod = prod/(mod1 * mod2);
		return prod;
	}
}
