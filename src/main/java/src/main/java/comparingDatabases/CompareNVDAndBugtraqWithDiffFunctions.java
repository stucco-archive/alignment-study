//program is comparing two documents: NVD and Bugtraq to find most similar objects
//NVD document is stored in JSONArray arrayOne, and Bugtraq in JSONArray arrayTwo
//the difference is that NVD and Bugtraq are using different time format, so
//we have to use different converting functions.
//and some fields have the same content, but called slightly different, for instance "vulnerableSoftware" and "Vulnerable".

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CompareNvdAndBugtraqWithDiffFunctions	{
	
	private FileReader readerOne;
	private FileReader readerTwo;
	private JSONParser parserOne;
	private JSONParser parserTwo;
	private JSONArray arrayOne;
	private JSONArray arrayTwo;

	public CompareNvdAndBugtraqWithDiffFunctions(String[] args)	{

		try {
			readerOne = new FileReader (args[0]);
			readerTwo = new FileReader (args[1]);
			parserOne = new JSONParser ();
			parserTwo = new JSONParser ();
			arrayOne = (JSONArray)parserOne.parse(readerOne); //JSON array from NVD database
			arrayTwo = (JSONArray)parserTwo.parse(readerTwo); //JSON array from bugtraq database
			
		//	double openPenalty = 1.0;
		//	double extendPenalty = 0.7;
			int qNumber = 2;

		//	new CompareWithAffineGap(arrayOne, arrayTwo, openPenalty, extendPenalty);
		//	new CompareWithWHIRL(arrayOne, arrayTwo);
		//	new CompareWithCosineAndQGrams(arrayOne, arrayTwo);
		//	new CompareWithDamerauLevenshtein(arrayOne, arrayTwo);
		//	new CompareWithJaroWinkler(arrayOne, arrayTwo);
		//	new CompareWithQGrams(arrayOne, arrayTwo, qNumber);
			new CompareWithSmithWaterman(arrayOne, arrayTwo);

		} catch (FileNotFoundException e)	{
			e.printStackTrace();
		} catch (IOException e)		{
			e.printStackTrace();
		} catch (ParseException e)	{
			e.printStackTrace();
		}		
	}
			
	public static void main (String[] args)	{
		
		new CompareNvdAndBugtraqWithDiffFunctions (args);
	}	
}
