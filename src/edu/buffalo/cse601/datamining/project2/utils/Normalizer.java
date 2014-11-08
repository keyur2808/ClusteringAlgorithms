package edu.buffalo.cse601.datamining.project2.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import edu.buffalo.cse601.datamining.project2.inputReader.ReadIn;
import edu.buffalo.cse601.datamining.project2.pojo.Point;

public class Normalizer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Point> points = new ArrayList<Point>();
		TreeMap<Integer, Integer> groundTruth = ReadIn.readIn("Input/dataset2.txt", points);
		int numDimensions = points.get(0).getCoords().length;
		// Normalize
		points = Normalizer.normalize(points, numDimensions);
		File outFile = new File("Input/dataset2_norm.txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(outFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			for (int i = 0; i < points.size(); i++) {
				Point point = points.get(i);
				int geneId = Integer.valueOf(point.getGeneId());
				bw.write(geneId + "");
				bw.write("\t");
				bw.write(groundTruth.get(geneId).intValue() + "");
				bw.write("\t");
				double[] coords = point.getCoords();
				for (int j = 0; j < coords.length; j++) {
					bw.write(coords[j] + "");
					bw.write("\t");
				}
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Point> normalize(ArrayList<Point> points, int numDimensions) {
		double[][] data = new double[points.size()][numDimensions];
		double[] min = new double[numDimensions];
		Arrays.fill(min, Double.MAX_VALUE);
		double[] max = new double[numDimensions];
		Arrays.fill(max, -Double.MAX_VALUE);
		for (int i = 0; i < points.size(); i++) {
			data[i] = points.get(i).getCoords();
			for (int j = 0; j < numDimensions; j++) {
				if (data[i][j] < min[j])
					min[j] = data[i][j];
				if (data[i][j] > max[j])
					max[j] = data[i][j];
			}
		}
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < numDimensions; j++) {
				if (max[j] == min[j])
					data[i][j] = 0;
				else
					data[i][j] = (data[i][j] - min[j]) / (max[j] - min[j]);
			}
			points.get(i).setCoords(data[i]);
		}
		return points;
	}

	public static double[][] normalize(double[][] data, int numDimensions) {
		double[] min = new double[numDimensions];
		Arrays.fill(min, Double.MAX_VALUE);
		double[] max = new double[numDimensions];
		Arrays.fill(max, -Double.MAX_VALUE);
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < numDimensions; j++) {
				if (data[i][j] < min[j])
					min[j] = data[i][j];
				if (data[i][j] > max[j])
					max[j] = data[i][j];
			}
		}
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < numDimensions; j++) {
				if (max[j] == min[j])
					data[i][j] = 0;
				else
					data[i][j] = (data[i][j] - min[j]) / (max[j] - min[j]);
			}
		}
		return data;
	}
}
