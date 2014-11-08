package edu.buffalo.cse601.datamining.project2.datastructures;

import java.util.ArrayList;

public class Cluster {

	private ArrayList<Cluster> childClusters = new ArrayList<Cluster>();
	private ArrayList<GeneExpression> expressions = new ArrayList<GeneExpression>();

	public Cluster() {
	}

	public ArrayList<Cluster> getChildClusters() {
		return childClusters;
	}

	public void setChildClusters(ArrayList<Cluster> childClusters) {
		this.childClusters = childClusters;
	}

	public ArrayList<GeneExpression> getExpressions() {
		return expressions;
	}

	public void setExpressions(ArrayList<GeneExpression> expressions) {
		this.expressions = expressions;
	}

	public void add(Cluster cluster) {
		this.childClusters.add(cluster);

	}

	public void addAll(ArrayList<GeneExpression> exp) {
		this.expressions.addAll(exp);
	}

	public void add(GeneExpression geneExpression) {
		this.expressions.add(geneExpression);

	}

}
