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
				
import org.json.*;
		
public class CompareNVDAndBugtraqWithDiffFunctions	{
	
	private FileReader readerOne;
	private FileReader readerTwo;
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;
	private Map <String, Long> duration;
	private EntropyCalculation entropyOne;
	private EntropyCalculation entropyTwo;
	private ROC roc;

	public CompareNVDAndBugtraqWithDiffFunctions(String[] args)	{
									
		try {							
			String line = new String();
			String text1 = new String();
			String text2 = new String();														

			InputStream i1 = CompareNVDAndBugtraqWithDiffFunctions.class.getClassLoader().getResourceAsStream(args[0]);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(i1));
			while ((line = br1.readLine()) != null)
				text1 = text1 + line;
			
			InputStream i2 = CompareNVDAndBugtraqWithDiffFunctions.class.getClassLoader().getResourceAsStream(args[1]);
			BufferedReader br2 = new BufferedReader(new InputStreamReader(i2));
			while ((line = br2.readLine()) != null)
				text2 = text2 + line;
				
			arrayOne = new JSONArray(text1);
			arrayTwo = new JSONArray(text2);
			
			entropyOne = new EntropyCalculation(arrayOne);
			entropyTwo = new EntropyCalculation(arrayTwo);

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
				method.printMatchTree(matchTree,  40, "outputFile.txt");
				roc = new ROC (matchTree, entropyOne.getEntropy(), entropyTwo.getEntropy(), method.getName());
				System.out.println(roc.getChartData());
				chart.addNewChart(method.getName(), roc.getChartData());
			//	chart.drawChart(); 
			}

			chart.drawChart();
			
			for (String key : duration.keySet())
				System.out.println(key + " duration = " + duration.get(key)); 
			
		} catch (IOException e)	{
			e.printStackTrace();		
		} catch (JSONException e)	{
			e.printStackTrace();
		}		
	}
			
//	public static void main (String[] args)	{
		
//		new CompareNVDAndBugtraqWithDiffFunctions (args);
//	}	
}
