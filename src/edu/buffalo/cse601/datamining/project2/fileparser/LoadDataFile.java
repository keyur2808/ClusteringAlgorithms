package edu.buffalo.cse601.datamining.project2.fileparser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import edu.buffalo.cse601.datamining.project2.datastructures.GeneExpression;

public class LoadDataFile {

	private String fileName = null;
	private int vectorDim = -1;
	private TreeMap<Integer, Integer> geneExpressionDefaultCluster = new TreeMap<>();
	private TreeMap<Integer, List<Double>> geneIdExpressionsMap = new TreeMap<>();
	
	public int getVectorDim() {
		return vectorDim;
	}

	public void setVectorDim(int vectorDim) {
		this.vectorDim = vectorDim;
	}

	
	public TreeMap<Integer, Integer> getGeneExpressionDefaultCluster() {
		return geneExpressionDefaultCluster;
	}

	private ArrayList<Double> attributes = new ArrayList<>();
	private List<GeneExpression> geneExpression = new ArrayList<>();

	public boolean parseFile(int  normalized) {
		ArrayList<Double> maxAttributes = new ArrayList<>();
		ArrayList<Double> minAttributes = new ArrayList<>();
		boolean success = false;
		FileReader reader = null;
		CSVParser parser = null;
		try {
			reader = new FileReader(fileName);
			parser = new CSVParser(reader, CSVFormat.TDF);
			int geneId = -1;
			int defaultClusterId = -2;
			List<Double> geneExpressionsList = null;

			List<CSVRecord> records = parser.getRecords();

			int recordSize = records.get(0).size() - 2;
			for (int i = 0; i < recordSize; i++) {
				maxAttributes.add(Double.MIN_VALUE);
				minAttributes.add(Double.MAX_VALUE);
			}

			for (CSVRecord record : records) {

				GeneExpression exp = new GeneExpression();

				if (record.get(0) != null && record.get(0) != "") {
					geneId = Integer.parseInt(record.get(0));
					exp.setGeneExpressionId(geneId);//Get geneId from line
				}

				if (record.get(1) != null && record.get(1) != "") {
					defaultClusterId = Integer.parseInt(record.get(1));
					exp.setDeafultClusterId(defaultClusterId);//Get default clusterId from file
				}

				geneExpressionsList = new ArrayList<Double>();
				for (int i = 2; i < record.size(); i++) {//Record Size =columns in row exp values from third col onwards
					String expressionValue = record.get(i);
					if (expressionValue != null && expressionValue != "") {
						geneExpressionsList.add(Double.valueOf(expressionValue));

						if (maxAttributes.get(i - 2) < Double.valueOf(expressionValue)) {
							maxAttributes.set(i - 2, Double.valueOf(expressionValue));
						}

						if (minAttributes.get(i - 2) > Double.valueOf(expressionValue)) {
							minAttributes.set(i - 2, Double.valueOf(expressionValue));
						}

					}

				}

				exp.setExpressionValues((ArrayList<Double>) geneExpressionsList);
				geneExpression.add(exp);

				geneExpressionDefaultCluster.put(geneId, defaultClusterId);
				geneIdExpressionsMap.put(geneId, geneExpressionsList);
				vectorDim = geneExpressionsList.size();
			}

			double mx = 0, min = 0;
			for (int i = 0; i < recordSize; i++) {
				mx = maxAttributes.get(i);
				min = minAttributes.get(i);
				attributes.add(mx - min);
			}

			if(normalized==1){
				normalizedMap(attributes,minAttributes);
			}
			
			parser.close();
			reader.close();
			success = true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	
	public void normalizedMap(List<Double>denom,List<Double>min){
	
		for(Entry<Integer,List<Double>>entry:geneIdExpressionsMap.entrySet()){
			for(int i=0;i<min.size();i++){
				if(denom.get(i)==0){
					denom.set(i,1.00);
				}
				double tmp=(entry.getValue().get(i)-min.get(i))/denom.get(i);
				entry.getValue().set(i, tmp);
			}
		}
		
	}
	
	public List<GeneExpression> getGeneExpressions() {
		return geneExpression;
	}

	public void setGeneExpressions(List<GeneExpression> geneExpression) {
		this.geneExpression = geneExpression;
	}

	public TreeMap<Integer, List<Double>> getGeneIdExpressionsMap() {
		return geneIdExpressionsMap;
	}

	public ArrayList<Double> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<Double> attributes) {
		this.attributes = attributes;
	}

	public LoadDataFile(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setGeneExpressionDefaultCluster(TreeMap<Integer, Integer> geneExpressionDefaultCluster) {
		this.geneExpressionDefaultCluster = geneExpressionDefaultCluster;
	}

	public void setGeneIdExpressionsMap(TreeMap<Integer, List<Double>> geneIdExpressionsMap) {
		this.geneIdExpressionsMap = geneIdExpressionsMap;
	}

}
