import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.sf.json.JSONSerializer;
import net.sf.json.JSON;

public class ROC {

	private JSONObject entropyOne;
	private JSONObject entropyTwo;
	private JSONObject entropyAverage;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;
	private List chartData;
	private ROCChart roc;
	private double entropySumOne;
	private double entropySumTwo;
	private double entropyTotal;
	private double threshold;
	private double area;
	private double maxKey;
	private int truePositive;
	private int falsePositive;
	private int lessTruePositive;
	private int lessFalsePositive;
	private int count;

	private class Point	{
		
			Point (double x, double y)	{
				this.x = x;
				this.y = y;	
			}
			double x;
			double y;
		}
	//constructor
	public ROC (TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree, JSONObject entropyOne, JSONObject entropyTwo)	{
		
		entropyAverage = new JSONObject();
		roc = new ROCChart("ROC", "Data Comparison");
		chartData = new ArrayList();
		this.entropyOne = entropyOne;
		this.entropyTwo = entropyTwo;
		this.matchTree = matchTree;
		maxKey = 0.0;

	//	calculateEntropyAverage();
	//	calculateEntropyTotal();
		setMaxKey();
		setThresholdAndCalculateData();
		calculateArea();
		System.out.println("area = " + area);
	}
	
	//calculating entropy average for every field beetween two docs, and adding to entreeAverage tree
	void calculateEntropyAverage()	{

		try {
			System.out.println(entropyOne);
			System.out.println(entropyTwo);
			entropyAverage.put("description", ((double)entropyOne.get("description") + (double)entropyTwo.get("description"))/2.0);
			entropyAverage.put("vulnerableSoftware", ((double)entropyOne.get("vulnerableSoftware") + (double)entropyTwo.get("Vulnerable"))/2.0);
			entropyAverage.put("publishedDate", ((double)entropyOne.get("publishedDate") + (double)entropyTwo.get("publishedDate"))/2.0);
			entropyAverage.put("modifiedDate", ((double)entropyOne.get("modifiedDate") + (double)entropyTwo.get("modifiedDate"))/2.0);
			entropyAverage.put("references", ((double)entropyOne.get("references") + (double)entropyTwo.get("references"))/2.0);
		//	entropyAverage.put("idAndClass", ((double)entropyOne.get("idAndClassSimilarity") + (double)entropyTwo.get("idAndClassSimilarity"))/2.0);
		} catch	(NumberFormatException e)	{
			e.printStackTrace();
		}
	}

	//sum of entropies from entropyAverage tree 
	void calculateEntropyTotal()	{
		
		entropyTotal = 0.0;

		for (Object key : entropyAverage.keySet())	{
			entropyTotal = entropyTotal + (double)entropyAverage.get(key);	
		}

		System.out.println("entropyTotal = " + " " + entropyTotal);
	}

	//mexKey is the largest similarity score between two objects from two databases
	void setMaxKey()	{

		count = 0;

		for (Object key : matchTree.keySet())	{
			if (maxKey < (double)key)
				maxKey = (double)key;
		}
		System.out.println("maxKey = " + maxKey);
	}

	//threshold is the fraction of maxKey (largest similarity score)
	void setThresholdAndCalculateData()	{
		
	//	for (double i = 50.0; i <= 100.0; i = i + 5.0)	{ 
	//		threshold = (entropyTotal/100.0) * i;
	//		System.out.println("threshold = " + threshold);
	//		calculateROC();	
	//	}

		//for every chosen threshold calculate  ROC data (true positive and false positive)
		for (double i = 0.1; i < maxKey; i = i + 0.01)	{
			threshold = i;
			System.out.println("threshold = " + threshold);
			calculateROC();	
		}
	}

	void calculateROC()	{
		
		double similarity;
		truePositive = 0;
		falsePositive = 0;
		lessTruePositive = 0;
		lessFalsePositive = 0;

		for (Object key : matchTree.keySet())	{
				ArrayList<ObjectsSimilarity> list = (ArrayList<ObjectsSimilarity>) matchTree.get((double)key);
				for (ObjectsSimilarity os : list)	
					calculateChartData(os);
		}
														
		chartData.add(new Point((double)truePositive/(double)(truePositive + lessTruePositive),  
				1.0 - ((double)falsePositive/(double)(falsePositive + lessFalsePositive)))); 
	//	System.out.println((double)truePositive/(double)(truePositive + lessTruePositive) +" "+  
	//				(1.0 - ((double)falsePositive/(double)(falsePositive + lessFalsePositive)))); 
		System.out.println("truePositive = " + truePositive); 
		System.out.println("falsePositive = " + falsePositive); 
	}
	
	void calculateChartData(ObjectsSimilarity os)	{

		double similarityTotal = 0.0;

		similarityTotal = similarityTotal + os.descriptionSimilarity;
		similarityTotal = similarityTotal + os.publTimeSimilarity;
		similarityTotal = similarityTotal + os.modifTimeSimilarity;
		similarityTotal = similarityTotal + os.referenceSimilarity;
		similarityTotal = similarityTotal + os.softwareSimilarity;
	//	similarityTotal = similarityTotal + os.idAndClassSimilarity * (double)entropyAverage.get("idAndClass");
	//	if ((similarityTotal/entropyTotal) * 100 >= threshold)	{
		if (similarityTotal >= threshold)	{
			if (os.objectTwo.get("CVE").toString().length() > 13)	{
				boolean positive = false;
				for (int i = 0; i <= os.objectTwo.get("CVE").toString().length() - 13; i = i + 13)	{
					if (os.objectTwo.get("CVE").toString().substring(i,i + 13).equals(os.objectTwo.get("_id")))	{
						positive = true; 
						truePositive++;
					}
				}
				if (!positive) falsePositive++;
			}
			else if (os.objectOne.get("_id").equals(os.objectTwo.get("CVE")))	truePositive++;
			else	falsePositive++;		
			
		}
		else {
			if (os.objectTwo.get("CVE").toString().length() > 13)	{
				boolean positive = false;
				for (int i = 0; i <= os.objectTwo.get("CVE").toString().length() - 13; i = i + 13)	{
					if (os.objectTwo.get("CVE").toString().substring(i,i + 13).equals(os.objectTwo.get("_id")))	{
						positive = true; 
						lessTruePositive++;
					}
				}
				if (!positive) lessFalsePositive++;
			}
			else if (os.objectOne.get("_id").equals(os.objectTwo.get("CVE")))	lessTruePositive++;
			else	lessFalsePositive++;		
		}
	}

	void calculateArea()	{

		roc.addData(0.0, 0.0);
		for (int i = 0; i < chartData.size(); i++)	{
			Point point = (Point) chartData.get(i);	
			roc.addData(point.x, point.y);
		}
		roc.drawChart();
		area = roc.calculateArea();
	}
}
