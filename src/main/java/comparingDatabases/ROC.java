package alignmentStudy;

import java.io.*;
import java.util.*;

import org.json.*;

public class ROC {

	private JSONObject entropyOne;
	private JSONObject entropyTwo;
	private JSONObject entropyAverage;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;
	private ArrayList<Point> chartData;
	private Map<Double, Point> trueFalseMap;
	private double entropyTotal, threshold, area,  maxKey;
	private int truePositive, falsePositive, lessTruePositive, lessFalsePositive, count;

	public ROC (TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree, JSONObject entropyOne, JSONObject entropyTwo, String functionName)	{
		
		chartData = new ArrayList<Point>();
		trueFalseMap = new HashMap<Double, Point>();
		entropyAverage = new JSONObject();
		this.entropyOne = entropyOne;
		this.entropyTwo = entropyTwo;
		this.matchTree = matchTree;
		
		try {
			calculateEntropyAverage();
			calculateMaxKey();
			setThreshold();								
			calculateArea();
		} catch (JSONException e)	{
			e.printStackTrace();
		}
	}

	void calculateEntropyAverage()	throws JSONException {

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
		} catch	(NumberFormatException e)	{
			e.printStackTrace();
		}
	}

	void calculateEntropyTotal() throws JSONException {
		
		entropyTotal = 0.0;

		Iterator it = entropyAverage.keys();						
		while (it.hasNext())	{
			String key = (String) it.next();
			entropyTotal = entropyTotal + new Double (entropyAverage.get(key).toString());	
		}

	}

	void calculateMaxKey() throws JSONException {
		
		maxKey = 0;
	
		double max;
		for (Double key : matchTree.keySet())	{
			ArrayList <ObjectsSimilarity> list = matchTree.get(new Double(key));
			for (ObjectsSimilarity p : list)	{
				max = p.descriptionSimilarity * new Double(entropyAverage.get("description").toString()) +
					p.publTimeSimilarity * new Double(entropyAverage.get("publishedDate").toString()) +
					p.modifTimeSimilarity * new Double(entropyAverage.get("modifiedDate").toString()) +
					p.referenceSimilarity * new Double(entropyAverage.get("references").toString()) +
					p.softwareSimilarity * new Double(entropyAverage.get("vulnerableSoftware").toString());
				if (max > maxKey)	maxKey = max;
			}
		}
	}

	void setThreshold() throws JSONException	{
							
		for (double i = 0.0; i <= maxKey; i = i + 0.1)	{
			threshold = i;
			System.out.println("threshold = " + threshold);
			calculateROC();	
		}
	}

	void calculateROC() throws JSONException {
		
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
		System.out.println("turePositive = " + truePositive);
		System.out.println("falsePositive = " + falsePositive);
		addTrueFalseMap (truePositive, falsePositive);
	}

	void calculateChartData(ObjectsSimilarity os) throws JSONException {

		double similarityTotal = 0.0;
		similarityTotal = similarityTotal + os.descriptionSimilarity * new Double (entropyAverage.get("description").toString());
		similarityTotal = similarityTotal + os.publTimeSimilarity * new Double (entropyAverage.get("publishedDate").toString());
		similarityTotal = similarityTotal + os.modifTimeSimilarity * new Double (entropyAverage.get("modifiedDate").toString());
		similarityTotal = similarityTotal + os.referenceSimilarity * new Double (entropyAverage.get("references").toString());
		similarityTotal = similarityTotal + os.softwareSimilarity * new Double (entropyAverage.get("vulnerableSoftware").toString());
		if (similarityTotal >= threshold)	{
			if (os.objectTwo.has("CVE") && os.objectOne.has("_id"))	{
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
				else {									
					if (os.objectOne.get("_id").equals(os.objectTwo.get("CVE")))	
						truePositive++;
				}
			}
			else	falsePositive++;		
			
		}
		else {									
			if (os.objectTwo.has("CVE") && os.objectOne.has("_id"))	{
				if (os.objectTwo.get("CVE").toString().length() > 13)	{
					boolean positive = false;
					for (int i = 0; i <= os.objectTwo.get("CVE").toString().length() - 13; i = i + 13)	{
						if (os.objectTwo.get("CVE").toString().substring(i,i + 13).equals(os.objectOne.get("_id")))	{
							positive = true; 
							lessTruePositive++;
						}
					}
					if (!positive) lessFalsePositive++;
				}
				else	{
					if (os.objectOne.get("_id").equals(os.objectTwo.get("CVE")))	
						lessTruePositive++;
				}
			}
			else	lessFalsePositive++;		
		}
	}
		
	void calculateArea()  {

                area = 0.0;
					
                Collections.sort(chartData, new Point());
                for (int i = 0; i < chartData.size() - 1; i++)    {
                        Point p1 = (Point) chartData.get(i);
                        Point p2 = (Point) chartData.get(i + 1);
                        area = area + ((p2.x - p1.x) * p2.y - ((p2.x - p1.x) * (p2.y - p1.y))/2.0);
                }
        }
	
	ArrayList<Point> getChartData()	{
	
		return chartData;
	}
	
	double getMaxKey()	{
		
		return maxKey;
	}
	
	double getArea()	{
	
		return area;
	}
	
	void addTrueFalseMap (int truePositive, int falsePositive)	{
	
		Point p = new Point(truePositive, falsePositive);
		trueFalseMap.put(threshold, p);
	}

	Map<Double, Point>  getTrueFalsePositive ()	{
		
		return trueFalseMap;
                
	}
}
