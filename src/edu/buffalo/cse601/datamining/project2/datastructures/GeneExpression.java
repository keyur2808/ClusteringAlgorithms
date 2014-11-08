package edu.buffalo.cse601.datamining.project2.datastructures;

import java.util.ArrayList;

public class GeneExpression {

	private int geneId;
	private ArrayList<Double> expressionValues;
	private int defaultClusterId;

	public GeneExpression(int geneExpressionId, int defaultClusterId, ArrayList<Double> expressionValues) {
		this.geneId = geneExpressionId;
		this.defaultClusterId = defaultClusterId;
		this.expressionValues = expressionValues;
	}

	public GeneExpression() {

	}

	public int getDeafultClusterId() {
		return defaultClusterId;
	}

	public void setDeafultClusterId(int deafultClusterId) {
		this.defaultClusterId = deafultClusterId;
	}

	public int getGeneExpressionId() {
		return geneId;
	}

	public void setGeneExpressionId(int geneExpressionId) {
		this.geneId = geneExpressionId;
	}

	public ArrayList<Double> getExpressionValues() {
		return expressionValues;
	}

	public void setExpressionValues(ArrayList<Double> expressionValues) {
		this.expressionValues = expressionValues;
	}

    public String toString(){
    	return String.valueOf(geneId);
    }
}
