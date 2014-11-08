package edu.buffalo.cse601.datamining.project2.kmeansMR;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class Cluster implements Writable {
	private DoubleWritable[] coords;
	private DoubleWritable memberCount;
	private IntWritable centroidID;
	private DoubleWritable clusterQuality;

	public Cluster(int size) {
		coords = new DoubleWritable[size];
		memberCount = new DoubleWritable();
		centroidID = new IntWritable();
		clusterQuality = new DoubleWritable();
	}

	public Cluster(IntWritable centroidID, DoubleWritable[] coords) {
		this.centroidID = centroidID;
		this.coords = coords;
		memberCount = new DoubleWritable();
		clusterQuality = new DoubleWritable();
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		centroidID.readFields(arg0);
		for(int i = 0; i < coords.length; i++)
			coords[i].readFields(arg0);
		memberCount.readFields(arg0);
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		centroidID.write(arg0);
		for(int i = 0; i < coords.length; i++)
			coords[i].write(arg0);
		memberCount.write(arg0);
	}

	/**
	 * @return the coords
	 */
	public DoubleWritable[] getCoords() {
		return coords;
	}

	/**
	 * @param coords the coords to set
	 */
	public void setCoords(DoubleWritable[] coords) {
		this.coords = coords;
	}

	/**
	 * @return the memberCount
	 */
	public DoubleWritable getMemberCount() {
		return memberCount;
	}

	/**
	 * @param memberCount the memberCount to set
	 */
	public void setMemberCount(DoubleWritable memberCount) {
		this.memberCount = memberCount;
	}

	/**
	 * @return the centroidID
	 */
	public IntWritable getCentroidID() {
		return centroidID;
	}

	/**
	 * @param centroidID the centroidID to set
	 */
	public void setCentroidID(IntWritable centroidID) {
		this.centroidID = centroidID;
	}

	/**
	 * @return the clusterQuality
	 */
	public DoubleWritable getClusterQuality() {
		return clusterQuality;
	}

	/**
	 * @param clusterQuality the clusterQuality to set
	 */
	public void setClusterQuality(DoubleWritable clusterQuality) {
		this.clusterQuality = clusterQuality;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(centroidID.get());
		sb.append(":");
		for (DoubleWritable coord : coords) {
			sb.append(coord.get());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(":");
		sb.append(clusterQuality);
		sb.append(":");
		sb.append(memberCount);
		return sb.toString();
	}
}