package edu.buffalo.cse601.datamining.project2.plots;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import PCA.Class1;

import com.google.common.primitives.Doubles;
import com.mathworks.toolbox.javabuilder.MWException;



public class PCAPlot implements Runnable{

	private String fileName = null;
	private Map<Integer,Integer>geneClusters=null;
	private Map<Integer, List<Double>> geneDataValues=null;
	private String clusteringAlgoName=null;
	private Map<Integer,Integer>colorMatrix=null;
	
	public PCAPlot(String fileName) {
		this.fileName = fileName;
	}

	public PCAPlot(Map<Integer, List<Double>> geneMap,String clusteringAlgoName,Map<Integer,Integer>colorMatrix) {
		this.geneDataValues=geneMap;
		this.clusteringAlgoName=clusteringAlgoName;
		this.colorMatrix=colorMatrix;
	}

	
	@Override
	public void run() {
		Class1 pcaInput = null;
		try {
			pcaInput = new Class1();
			if (fileName != null) {
				
			  pcaInput.PCA_Matlab(fileName);
			}else{
					    
		    int i=0;
		    double[][] geneFeatures=new double[geneDataValues.size()][geneDataValues.get(1).size()];
			for (Entry<Integer,List<Double>>entry :geneDataValues.entrySet()){
					geneFeatures[i]=Doubles.toArray(entry.getValue());
					i++;
				}
			
			i=0;
			int[]colors=new int[colorMatrix.size()];
			for (Entry<Integer,Integer>entry :colorMatrix.entrySet()){
			      colors[i]=entry.getValue();
			      i++;
			}
			 
			 pcaInput.PCA_Algo(geneFeatures, clusteringAlgoName,colors);
			}
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
