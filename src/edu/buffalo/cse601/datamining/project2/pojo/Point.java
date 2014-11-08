package edu.buffalo.cse601.datamining.project2.pojo;

public class Point {

	private String geneId;
	private double[] coords;
	private double distFromCentroid;

	/**
	 * @return the geneId
	 */
	public String getGeneId() {
		return geneId;
	}

	/**
	 * @param geneId
	 *            the geneId to set
	 */
	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}

	/**
	 * @return the coords
	 */
	public double[] getCoords() {
		return coords;
	}

	/**
	 * @param coords
	 *            the coords to set
	 */
	public void setCoords(double[] coords) {
		this.coords = coords;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (double coord : coords) {
			sb.append(coord);
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(";");
		sb.append(distFromCentroid);
		return sb.toString();

	}

	/**
	 * @return the distFromCentroid
	 */
	public double getDistFromCentroid() {
		return distFromCentroid;
	}

	/**
	 * @param distFromCentroid the distFromCentroid to set
	 */
	public void setDistFromCentroid(double distFromCentroid) {
		this.distFromCentroid = distFromCentroid;
	}
}
