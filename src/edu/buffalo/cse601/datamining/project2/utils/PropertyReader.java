package edu.buffalo.cse601.datamining.project2.utils;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

	/**
	 * @param args
	 * @return 
	 */
	public static Properties getProperties(String filePath) {
		Properties properties = new Properties();
		File inFile = new File(filePath);
		try {
			properties.load(new FileReader(inFile.getAbsoluteFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

}
