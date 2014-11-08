package edu.buffalo.cse601.datamining.project2.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import edu.buffalo.cse601.datamining.project2.pojo.Point;

public class GetGeneMap {

	public static TreeMap<Integer, List<Double>> getGeneMap(ArrayList<Point> points) {
		TreeMap<Integer, List<Double>> geneMap = new TreeMap<Integer, List<Double>>();
		for (Point point : points) {
			List<Double> coordList = new ArrayList<Double>();
			for (double coord : point.getCoords()) {
				coordList.add(coord);
			}
			geneMap.put(Integer.valueOf(point.getGeneId()), coordList);
		}
//		System.out.println(geneMap);
		return geneMap;

	}
}
