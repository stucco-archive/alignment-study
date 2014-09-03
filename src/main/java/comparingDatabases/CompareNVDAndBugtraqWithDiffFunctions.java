package alignmentStudy;

//program is comparing two documents: NVD and Bugtraq to find most similar objects
//NVD document is stored in JSONArray arrayOne, and Bugtraq in JSONArray arrayTwo
//the difference is that NVD and Bugtraq are using different time format, so
//we have to use different converting functions.
//and some fields have the same content, but called slightly different, for instance "vulnerableSoftware" and "Vulnerable".

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	private Map <String, Long> duration;
	private EntropyCalculation entropyOne;
	private EntropyCalculation entropyTwo;
	private ROC roc;

	public CompareNVDAndBugtraqWithDiffFunctions(String[] args)	{

		try {

			readerOne = new FileReader (args[0]);
			readerTwo = new FileReader (args[1]);
			parserOne = new JSONParser ();
			parserTwo = new JSONParser ();
			arrayOne = (JSONArray)parserOne.parse(readerOne); //JSON array from NVD database
			arrayTwo = (JSONArray)parserTwo.parse(readerTwo); //JSON array from bugtraq database
			entropyOne = new EntropyCalculation(args[0]);
			entropyTwo = new EntropyCalculation(args[1]);
			ROCChart chart = new ROCChart("ROC");
			matchTree = new TreeMap<Double, ArrayList<ObjectsSimilarity>>();
			duration = new HashMap<String, Long>();	
			
			long start;
			double openPenalty = 1.0, extendPenalty = 0.7;	//parameters for Affine Gap
			int qNumber = 2;	//paremeter for QGrams algorith

			List <ComparisonMethod>  methods = new ArrayList<ComparisonMethod>();
			
			start = System.currentTimeMillis();
			methods.add (new CompareWithWHIRL(arrayOne, arrayTwo));
			duration.put("WHIRL", System.currentTimeMillis() - start);

			start = System.currentTimeMillis();
			methods.add (new CompareWithCosineAndQGrams(arrayOne, arrayTwo));
			duration.put("Cosine + QGrams", System.currentTimeMillis() - start);
			
			start = System.currentTimeMillis();
			methods.add (new CompareWithDamerauLevenshtein(arrayOne, arrayTwo));
			duration.put("Damerau-Levenshtein", System.currentTimeMillis() - start);
			
			start = System.currentTimeMillis();
			methods.add (new CompareWithJaroWinkler(arrayOne, arrayTwo));
			duration.put("Jaro-Winkler", System.currentTimeMillis() - start);
			
			start = System.currentTimeMillis();
			methods.add (new CompareWithQGrams(arrayOne, arrayTwo, qNumber));
			duration.put("QGrams", System.currentTimeMillis() - start);
			
			start = System.currentTimeMillis();
			methods.add (new CompareWithSmithWaterman(arrayOne, arrayTwo)); 
			duration.put("Smith-Waterman", System.currentTimeMillis() - start);
			
			start = System.currentTimeMillis();
			methods.add (new CompareWithAffineGap(arrayOne, arrayTwo, openPenalty, extendPenalty));
			duration.put("Affine Gap", System.currentTimeMillis() - start);
		
			for (ComparisonMethod method : methods)	{
				matchTree = method.getMatchTree();
				roc = new ROC (matchTree, entropyOne.getEntropy(), entropyTwo.getEntropy(), method.getName());
				chart.addNewChart(method.getName(), roc.getChartData());
			}

			chart.drawChart();
			
			for (String key : duration.keySet())
				System.out.println(key + " duration = " + duration.get(key)); 
			
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
