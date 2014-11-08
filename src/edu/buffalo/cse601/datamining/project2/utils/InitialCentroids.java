package edu.buffalo.cse601.datamining.project2.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.google.common.primitives.Doubles;

public class InitialCentroids {

	private String fileName=null;
	private double[][] initialCentroids=null;
	
	
	public InitialCentroids(String fileName) {
		this.fileName=fileName;
	}
	
	public InitialCentroids() {
		//this.fileName=fileName;
	}
	
	//loadInitialCentroids
	
	public double[][] loadCentroids(ArrayList<Integer>rowIds,Map<Integer,ArrayList<Double>>geneDataValues){
		int features=geneDataValues.get(1).size();
		initialCentroids=new double[rowIds.size()][features];
		for (int i=0;i<rowIds.size();i++){
			initialCentroids[i]=Doubles.toArray(geneDataValues.get(rowIds.get(i)));
			
		}
		return initialCentroids;
	}
	
	
	public double[][] parseFile(){
		boolean success = false;
		FileReader reader = null;
		CSVParser parser = null;
		try {
			reader = new FileReader(fileName);
			parser = new CSVParser(reader, CSVFormat.TDF);
			int geneId = -1;
			int defaultClusterId = -2;
			List<CSVRecord> records = parser.getRecords();
			int recordSize = records.get(0).size()-1;
			initialCentroids=new double[records.size()][recordSize];
			int j=0;
			for (CSVRecord record : records) {
				
				for(int i=1;i<record.size();i++){
					initialCentroids[j][i-1]=Integer.parseInt(record.get(i));
				}
				j++;
			}	
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		try {
			parser.close();
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	return initialCentroids;
	}
	
	
}
