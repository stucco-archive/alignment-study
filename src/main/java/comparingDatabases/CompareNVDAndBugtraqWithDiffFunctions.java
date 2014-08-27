package alignmentStudy;

//program is comparing two documents: NVD and Bugtraq to find most similar objects
//NVD document is stored in JSONArray arrayOne, and Bugtraq in JSONArray arrayTwo
//the difference is that NVD and Bugtraq are using different time format, so
//we have to use different converting functions.
//and some fields have the same content, but called slightly different, for instance "vulnerableSoftware" and "Vulnerable".

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
			
public class CompareNVDAndBugtraqWithDiffFunctions	{
	
	private FileReader readerOne;
	private FileReader readerTwo;
	private JSONParser parserOne;
	private JSONParser parserTwo;
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;
	private Map <String, Double> areas;
	private Map <String, Long> duration;
	private EntropyCalculation entropyOne;
	private EntropyCalculation entropyTwo;
	private ROC roc;

	public CompareNVDAndBugtraqWithDiffFunctions(String[] args)	{

		try {

			FileWriter fw = new FileWriter(new File ("ComparisonTable.txt"));
			BufferedWriter bw = new BufferedWriter(fw);
			readerOne = new FileReader (args[0]);
			readerTwo = new FileReader (args[1]);
			parserOne = new JSONParser ();
			parserTwo = new JSONParser ();
			arrayOne = (JSONArray)parserOne.parse(readerOne); //JSON array from NVD database
			arrayTwo = (JSONArray)parserTwo.parse(readerTwo); //JSON array from bugtraq database
			entropyOne = new EntropyCalculation(args[0]);
			entropyTwo = new EntropyCalculation(args[1]);
			matchTree = new TreeMap<Double, ArrayList<ObjectsSimilarity>>();
			areas = new HashMap<String, Double>();	
			duration = new HashMap<String, Long>();	
			double openPenalty = 1.0, extendPenalty = 0.7;
			long duration, start;
			int qNumber = 2;
			ROCChart chart = new ROCChart("ROC");
			
			start = System.currentTimeMillis();
			CompareWithWHIRL method1 = new CompareWithWHIRL(arrayOne, arrayTwo);
			duration = System.currentTimeMillis() - start;
			System.out.println("duration = " + duration);
			matchTree = method1.getMatchTree();
			roc = new ROC (matchTree, entropyOne.getEntropy(), entropyTwo.getEntropy(), "WHIRL");
			chart.addNewChart("WHIRL", roc.getChartData());
		//	areas.put("WHIRL", roc.getArea());
		
		//	duration.put("WHIRL", new Long(duration));
			System.out.println(" **************************************************************** ");
			System.out.println();

			start = System.currentTimeMillis();
			CompareWithCosineAndQGrams method2 = new CompareWithCosineAndQGrams(arrayOne, arrayTwo);
			duration = System.currentTimeMillis() - start;
			System.out.println("duration = " + duration);
			matchTree = method2.getMatchTree();
			roc = new  ROC (matchTree, entropyOne.getEntropy(), entropyTwo.getEntropy(), "CosineAndQGrams");
			chart.addNewChart("CosineAndQGrams", roc.getChartData());
		//	areas.put("CosineAndQGrams", roc.getArea());
		//	duration.put("CosineAndQGrams", duration);
			System.out.println(" **************************************************************** ");
			System.out.println();
			start = System.currentTimeMillis();
			CompareWithDamerauLevenshtein method3 = new CompareWithDamerauLevenshtein(arrayOne, arrayTwo);
			duration = System.currentTimeMillis() - start;
			System.out.println("duration = " + duration);
			matchTree = method3.getMatchTree();
			roc = new ROC (matchTree, entropyOne.getEntropy(), entropyTwo.getEntropy(), "DamerauLevenshtein");
			chart.addNewChart("DamerauLevenshtein", roc.getChartData());
		//	areas.put("DamerauLevenshtein", roc.getArea());
		//	duration.put("DamerauLevenshtein", duration);
			System.out.println(" **************************************************************** ");
			System.out.println();
		
			start = System.currentTimeMillis();
			CompareWithJaroWinkler method4 = new CompareWithJaroWinkler(arrayOne, arrayTwo);
			duration = System.currentTimeMillis() - start;
			System.out.println("duration = " + duration);
			matchTree = method4.getMatchTree();
			roc = new ROC (matchTree, entropyOne.getEntropy(), entropyTwo.getEntropy(), "JaroWinkler");
			chart.addNewChart("JaroWinkler", roc.getChartData());
		//	areas.put("JaroWinkler", roc.getArea());
		//	duration.put("JaroWinkler", duration);
			System.out.println(" **************************************************************** ");
			System.out.println();
		
			start = System.currentTimeMillis();
			CompareWithQGrams method5 = new CompareWithQGrams(arrayOne, arrayTwo, qNumber);
			duration = System.currentTimeMillis() - start;
			System.out.println("duration = " + duration);
			matchTree = method5.getMatchTree();
			roc = new ROC (matchTree, entropyOne.getEntropy(), entropyTwo.getEntropy(), "QGrams");
			chart.addNewChart("QGrams", roc.getChartData());
		//	areas.put("QGrams", roc.getArea());
		//	duration.put("QGrams", duration);
			System.out.println(" **************************************************************** ");
			System.out.println();
		
			start = System.currentTimeMillis();
			CompareWithSmithWaterman method6 = new CompareWithSmithWaterman(arrayOne, arrayTwo);
			duration = System.currentTimeMillis() - start;
			System.out.println("duration = " + duration);
			matchTree = method6.getMatchTree();
			roc = new ROC (matchTree, entropyOne.getEntropy(), entropyTwo.getEntropy(), "SmithWaterman");
			chart.addNewChart("SmithWaterman", roc.getChartData());
		//	areas.put("SmithWaterman", roc.getArea());
		//	duration.put("SmithWaterman", duration);
			System.out.println(" **************************************************************** ");
			System.out.println();
		
			start = System.currentTimeMillis();
			CompareWithAffineGap method7 = new CompareWithAffineGap(arrayOne, arrayTwo, openPenalty, extendPenalty);
			duration = System.currentTimeMillis() - start;
			System.out.println("duration = " + duration);
			matchTree = method7.getMatchTree();
			roc = new ROC (matchTree, entropyOne.getEntropy(), entropyTwo.getEntropy(), "AffineGap");
			chart.addNewChart("AffineGap", roc.getChartData());
		//	areas.put("AffineGap", roc.getArea());
		//	duration.put("AffineGap", duration);
			System.out.println(" **************************************************************** ");
			System.out.println();
		//	for (String key : areas.keySet())	
		//		System.out.println(key + ":	area = " + areas.get(key)); 
		//
		//	for (String key : areas.keySet())	
		//		System.out.println(key + ":	area = " + duration.get(key)); 
		
			chart.drawChart();
		} catch (FileNotFoundException e)	{
			e.printStackTrace();
		} catch (IOException e)		{
			e.printStackTrace();
		} catch (ParseException e)	{
			e.printStackTrace();
		}		
	}
			
	public static void main (String[] args)	{
		
		new CompareNVDAndBugtraqWithDiffFunctions (args);
	}	
}
