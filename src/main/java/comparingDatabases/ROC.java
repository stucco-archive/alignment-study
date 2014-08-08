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
	private double w1, w2, w3, w4, w5;
	private int truePositive;
	private int falsePositive;
	private int lessTruePositive;
	private int lessFalsePositive;
	private int count;
	
	private class Point implements Comparator<Point>	{
	
		double x;
		double y;
		Point() {};
		
		Point(double x, double y)	{
			this.x = x;
			this.y = y;	
		}
		public int compare(Point one, Point two)	{
			return Double.compare(one.x, two.x);
		}
	}
																	
	public ROC (TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree, JSONObject entropyOne, JSONObject entropyTwo, String functionName)	{
		
		entropyAverage = new JSONObject();
		roc = new ROCChart("ROC", functionName);
		chartData = new ArrayList();
		this.entropyOne = entropyOne;
		this.entropyTwo = entropyTwo;
		this.matchTree = matchTree;
	
		calculateEntropyAverage();
		calculateEntropyTotal();
	//	calculateMaxKey();
		setThreshold();
		calculateArea();
		roc.drawChart();
		System.out.println("area = " + area);
	}
	
	void calculateEntropyAverage()	{

		try {
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

	void calculateEntropyTotal()	{
		
		entropyTotal = 0.0;

		for (Object key : entropyAverage.keySet())	{
			entropyTotal = entropyTotal + (double)entropyAverage.get(key);	
		}

	}

	void calculateMaxKey()	{
		
		maxKey = 0;

		for (Object key : matchTree.keySet())	{
			if (maxKey < (double) key)
				maxKey = (double)key;
		}

	}

	void setThreshold()	{
		
		for (double i = 0.0; i <= entropyTotal; i = i + 0.1)	{
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
				if ((double)key == 0.0) break;
				ArrayList<ObjectsSimilarity> list = (ArrayList<ObjectsSimilarity>) matchTree.get((double)key);
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
		similarityTotal = similarityTotal + os.descriptionSimilarity * (double)entropyAverage.get("description");
		similarityTotal = similarityTotal + os.publTimeSimilarity * (double)entropyAverage.get("publishedDate");
		similarityTotal = similarityTotal + os.modifTimeSimilarity * (double)entropyAverage.get("modifiedDate");
		similarityTotal = similarityTotal + os.referenceSimilarity * (double)entropyAverage.get("references");
		similarityTotal = similarityTotal + os.softwareSimilarity * (double)entropyAverage.get("vulnerableSoftware");
	//	similarityTotal = similarityTotal + os.idAndClassSimilarity * (double)entropyAverage.get("idAndClass");
	//	System.out.println("similarityTotal = " + similarityTotal);
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

	void calculateArea()	{

		roc.addData(0.0, 0.0);
		for (int i = 0; i < chartData.size(); i++)	{
			Point point = (Point) chartData.get(i);	
			roc.addData(point.x, point.y);
		}
		area = roc.calculateArea();
	}
	
/*	void calculateArea()	{
		
		area = 0.0;

		Collections.sort(chartData, new Point());
		for (int i = 0; i < chartData.size() - 1; i++)	{
			Point p1 = (Point) chartData.get(i);
			Point p2 = (Point) chartData.get(i + 1);
			area = area + ((p2.x - p1.x) * p2.y - ((p2.x - p1.x) * (p2.y - p1.y))/2.0); 
		}
	}
*/		
	double getArea()	{
		return area;
	}
}
