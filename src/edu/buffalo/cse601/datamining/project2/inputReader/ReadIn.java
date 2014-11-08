package edu.buffalo.cse601.datamining.project2.inputReader;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import edu.buffalo.cse601.datamining.project2.pojo.Point;


public class ReadIn {
	
	public static TreeMap<Integer, Integer> readIn(String filePath, ArrayList<Point> points) {
		TreeMap<Integer, Integer> groundTruth = new TreeMap<Integer, Integer>();
		File inFile = new File(filePath);
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(inFile.getAbsoluteFile());
			br = new BufferedReader(fr);
			String line;
			//			int count = 0;
			while ((line = br.readLine()) != null) {
				String[] lineArray = line.split("\t");
				String geneId = lineArray[0];
				String clusterId = lineArray[1];
				groundTruth.put(Integer.valueOf(geneId), Integer.valueOf(clusterId));
				double[] coords = new double[(lineArray.length - 2)];
				//				numDimensions = lineArray.length - 2;
				for (int i = 2; i < lineArray.length; i++) {
					coords[i - 2] = Double.valueOf(lineArray[i]);
				}
				Point point = new Point();
				point.setGeneId(geneId);
				point.setCoords(coords);
				points.add(point);
				//				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return groundTruth;
	}
}
