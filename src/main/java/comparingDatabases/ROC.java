package alignmentStudy;

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
//	private List chartData;
	private ArrayList<Point> chartData;
//	private ROCChart roc;
	private double entropySumOne;
	private double entropySumTwo;
	private double entropyTotal;
	private double threshold;
	private double area;
	private double maxKey;
	private double w1, w2, w3, w4, w5;
	private int truePositive;
	private int falsePositive;
	private int lessTruePositive;
	private int lessFalsePositive;
	private int count;
	
	public ROC (TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree, JSONObject entropyOne, JSONObject entropyTwo, String functionName)	{
		
		entropyAverage = new JSONObject();
		chartData = new ArrayList<Point>();
		this.entropyOne = entropyOne;
		this.entropyTwo = entropyTwo;
		this.matchTree = matchTree;
	
	//	calculateEntropyAverage();
	//	calculateEntropyTotal();
	//	setCount();
		calculateMaxKey();
		setThreshold();
		System.out.println("area = " + area);
	}
	
	void calculateEntropyAverage()	{

		try {
			entropyAverage.put("description", (new Double (entropyOne.get("description").toString()) + 
                                                          	new Double (entropyTwo.get("description").toString()))/2.0);
			entropyAverage.put("vulnerableSoftware", (new Double (entropyOne.get("vulnerableSoftware").toString()) + 
								new Double(entropyTwo.get("Vulnerable").toString()))/2.0);
			entropyAverage.put("publishedDate", (new Double (entropyOne.get("publishedDate").toString()) + 
								new Double (entropyTwo.get("publishedDate").toString()))/2.0);
			entropyAverage.put("modifiedDate", (new Double (entropyOne.get("modifiedDate").toString()) + 
								new Double (entropyTwo.get("modifiedDate").toString()))/2.0);
			entropyAverage.put("references", (new Double (entropyOne.get("references").toString()) + 
								new Double (entropyTwo.get("references").toString()))/2.0);
		//	entropyAverage.put("idAndClass", (new Double (entropyOne.get("idAndClassSimilarity").toString()) + 
		//						new Double (entropyTwo.get("idAndClassSimilarity").toString())/2.0);
		} catch	(NumberFormatException e)	{
			e.printStackTrace();
		}
	}

	void calculateEntropyTotal()	{
		
		entropyTotal = 0.0;

		for (Object key : entropyAverage.keySet())	{
			entropyTotal = entropyTotal + new Double (entropyAverage.get(key).toString());	
		}

	}

	void setCount()	{

		count = 0;

		for (Object key : matchTree.keySet())	{
			ArrayList<ObjectsSimilarity> list = (ArrayList<ObjectsSimilarity>) matchTree.get(new Double (key.toString()));
			count = count + ((ArrayList<ObjectsSimilarity>) matchTree.get(new Double (key.toString()))).size();
		}												
		System.out.println("count = " + count);
	}

	void calculateMaxKey()	{
		
		maxKey = 0;

		for (Object key : matchTree.keySet())	{
			ArrayList<ObjectsSimilarity> list = (ArrayList<ObjectsSimilarity>) matchTree.get(new Double (key.toString()));
			for (ObjectsSimilarity os : list)		{
				if (maxKey < os.descriptionSimilarity)
					maxKey = os.descriptionSimilarity;
			}
		}
	
	//	for (Object key : matchTree.keySet())	{
	//		if (maxKey < (double) key)	maxKey = (double) key;
	//	}

	}

	void setThreshold()	{
		
	//	for (double i = 50.0; i <= 100.0; i = i + 5.0)	{ 
	//		threshold = (entropyTotal/100.0) * i;
	//		System.out.println("threshold = " + threshold);
	//		calculateROC();	
	//	}

	//	for (double i = 0.0; i <= 1.0; i = i + 0.1)	{
		//	threshold = (i/entropyTotal) * 100;
		for (double i = 0.0; i <= maxKey; i = i + 0.1)	{
			threshold = i;
	//		System.out.println("threshold = " + threshold);
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
				if (new Double (key.toString()) == 0.0) break;
				ArrayList<ObjectsSimilarity> list = (ArrayList<ObjectsSimilarity>) matchTree.get(new Double (key.toString()));
				for (ObjectsSimilarity os : list)	
					calculateChartData(os);
		}
														
		chartData.add(new Point(((double)falsePositive/(double)(falsePositive + lessFalsePositive)),
					(double)truePositive/(double)(truePositive + lessTruePositive)));  
	//	System.out.println("truePositive = " + truePositive); 
	//	System.out.println("falsePositive = " + falsePositive); 
	}
	
	void calculateChartData(ObjectsSimilarity os)	{

		double similarityTotal = 0.0;																										
		similarityTotal = similarityTotal + os.descriptionSimilarity;// * (double)entropyAverage.get("description") * w1;
	//	similarityTotal = similarityTotal + os.publTimeSimilarity;// * (double)entropyAverage.get("publishedDate") * w2;
	//	similarityTotal = similarityTotal + os.modifTimeSimilarity;// * (double)entropyAverage.get("modifiedDate") * w3;
	//	similarityTotal = similarityTotal + os.referenceSimilarity;// * (double)entropyAverage.get("references") * w4;
	//	similarityTotal = similarityTotal + os.softwareSimilarity;// * (double)entropyAverage.get("vulnerableSoftware") * w5;
	//	similarityTotal = similarityTotal + os.idAndClassSimilarity * (double)entropyAverage.get("idAndClass") * w6;
	//	System.out.println("similarityTotal = " + similarityTotal);
	//	if ((similarityTotal/entropyTotal) * 100 >= threshold)	{
		if (similarityTotal >= threshold)	{
			if (os.objectTwo.get("CVE").toString().length() > 13)	{
				boolean positive = false;
				for (int i = 0; i <= os.objectTwo.get("CVE").toString().length() - 13; i = i + 13)	{
					if (os.objectTwo.get("CVE").toString().substring(i,i + 13).equals(os.objectOne.get("_id")))	{
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
		
	ArrayList<Point> getChartData()	{
		return chartData;
	}

	double getArea()	{
		return area;
	}
}
