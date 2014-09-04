package alignmentStudy;

import java.util.*;

import org.json.simple.JSONObject;

public class ROC {

	private JSONObject entropyOne;
	private JSONObject entropyTwo;
	private JSONObject entropyAverage;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;
	private ArrayList<Point> chartData;
	private double entropyTotal, threshold, area,  maxKey;
	private int truePositive, falsePositive, lessTruePositive, lessFalsePositive, count;
	
	public ROC (TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree, JSONObject entropyOne, JSONObject entropyTwo, String functionName)	{
		
		entropyAverage = new JSONObject();
		chartData = new ArrayList<Point>();
		this.entropyOne = entropyOne;
		this.entropyTwo = entropyTwo;
		this.matchTree = matchTree;
	
		calculateEntropyAverage();
		calculateEntropyTotal();
	//	calculateMaxKey();
		setThreshold();
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
		//						new Double (entropyTwo.get("idAndClassSimilarity").toString()))/2.0);
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

	void calculateMaxKey()	{
		
		maxKey = 0;
				
		for (Double key : matchTree.keySet())	{
			if (maxKey < (double) key)	maxKey = key;
		}

	}

	void setThreshold()	{
	
		for (double i = 0.0; i <= entropyTotal; i = i + 0.1)	{
			threshold = i;
			calculateROC();	
		}
	}

	void calculateROC()	{
		
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
	}
	
	void calculateChartData(ObjectsSimilarity os)	{

		double similarityTotal = 0.0;																										
		similarityTotal = similarityTotal + os.descriptionSimilarity * new Double (entropyAverage.get("description").toString());
		similarityTotal = similarityTotal + os.publTimeSimilarity * new Double (entropyAverage.get("publishedDate").toString());
		similarityTotal = similarityTotal + os.modifTimeSimilarity * new Double (entropyAverage.get("modifiedDate").toString());
		similarityTotal = similarityTotal + os.referenceSimilarity * new Double (entropyAverage.get("references").toString());
		similarityTotal = similarityTotal + os.softwareSimilarity * new Double (entropyAverage.get("vulnerableSoftware").toString());
	//	similarityTotal = similarityTotal + os.idAndClassSimilarity * new Double (entropyAverage.get("idAndClass").toString());
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
