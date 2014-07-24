//program is comparing two documents: NVD and Bugtraq to find most similar objectsd
//based on description, published time, modified time, and references, vulnerable software
//each property worth from 0.0 (have nothing in common) to 1.0 (the same)
//times are compared using difference in times represented in unix integer
//descriptions are compared with Smith Waterman algorithm

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CompareWithSmithWaterman	{
	
	private JSONObject objectOne;
	private JSONObject objectTwo;
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private RemoveStopWords rsw;
	private DateTimeFormat dtf;
	private Map<Integer, Map<String, Integer>>  mapOne; 	//size of JSON array
	private Map<Integer, Map<String, Integer>>  mapTwo; 	//size of JSON array
	private Map<String, Integer> allWords;
	private TreeMap<Double, JSONObject[]> matchTree;
	private char[] extraChars;	//used to remove extra chars from text and trim extra space

	public CompareWithSmithWaterman (JSONArray arrayOne, JSONArray arrayTwo)	{

		objectOne = new JSONObject ();
		objectTwo = new JSONObject ();
		matchTree = new TreeMap<Double, JSONObject[]>(Collections.reverseOrder());
		dtf = new DateTimeFormat();
		rsw = new RemoveStopWords("StopWords.txt");
		this.arrayOne = arrayOne;
		this.arrayTwo = arrayTwo;

		compare();
	}

	void compare()	{
			
		compareDatabases();		//comparing descripiton
		printMatchTree(10);		//print first 10 best matches
	}

	//function traversing arrays and comparing corresponding elements
	void compareDatabases()	{
		
		double similarityScore, descrSimilarity, publTimeSimilarity, modifTimeSimilarity, referenceSimilarity, softwareSimilarity;

		for (int i = 0; i < arrayOne.size(); i++)	{
			objectOne = (JSONObject) arrayOne.get(i);
			for (int j = 0; j < arrayTwo.size(); j++)	{
				objectTwo = (JSONObject) arrayTwo.get(j);
				descrSimilarity = compareDescriptions(objectOne.get("description"), objectTwo.get("description"));
				publTimeSimilarity = compareTime (i, j, "publishedDate");	//specifying type of time to compare
				modifTimeSimilarity = compareTime (i, j, "modifiedDate");
				referenceSimilarity = compareReferences (objectOne.get("references"), objectTwo.get("references"));
				softwareSimilarity = compareSoftware (objectOne.get("vulnerableSoftware"), objectTwo.get("Vulnerable"));
				similarityScore = descrSimilarity + publTimeSimilarity + modifTimeSimilarity + referenceSimilarity + softwareSimilarity;
				addToMatchTree (similarityScore, objectOne, objectTwo);
			}
		}
	}
	
	//compares descriptions using WHIRL or cosine similarity
	double compareDescriptions(Object descriptionOne, Object descriptionTwo)	{

		if (descriptionOne == null | descriptionTwo == null) return 0.0;

		SmithWaterman sw = new SmithWaterman();

		return sw.smithWatermanScore(descriptionOne.toString(), descriptionTwo.toString());
	}

	void stemAndRemoveStopWords(String description)	{
		
		PorterStemmer ps = new PorterStemmer();
		String[] str = description.split(" ");
		String newStr = new String();

		for (int j = 0; j < str.length; j++)  {
			if (rsw.containsString(str[j])) continue;	//removing stop words
			str[j] = str[j].toLowerCase();    
			str[j] = removeChars(str[j]);	//removes extra chars and trims extra space;
			ps.add(str[j].toCharArray(), str[j].length());
			ps.stem();
			newStr = newStr + ps.toString();
		}
	}
	
	//removing unnecessary chars from words
	//array of unnecessary chars can be modified in constructor
	String removeChars(String text)	{
	
		char[] myText = text.toCharArray();
		String newText = new String();
		boolean next;

		for (int i = 0; i < myText.length; i++)	{
			next = false;
			for (int j = 0; j < extraChars.length; j++)	{
				if (myText[i] == extraChars[j])	{
					next = true;
					break;
				}
			}
			if (next == false)	newText = newText + myText[i]; 
		}
		
		return newText;
	}
	
	//returns 0.0 if time is not specifyed in both objects
	double compareTime (int i, int j, String typeOfTime)	{
		
		long timeOne, timeTwo;

		objectOne = (JSONObject)arrayOne.get(i);
		objectTwo = (JSONObject)arrayTwo.get(j);
		if (objectOne.get(typeOfTime) != null && objectTwo.get(typeOfTime) != null)	{
			timeOne = dtf.formatNVDDateTime(objectOne.get(typeOfTime).toString());
			timeTwo = dtf.formatBugtraqDateTime(objectTwo.get(typeOfTime).toString());
			if (timeOne == timeTwo) return 1.0;
			else	return 1.0/(double)Math.abs(timeOne - timeTwo);
		}
		else return 0.0;
	}
	
	//return is between 0.0 (nothing in common) and 1.0
	double compareReferences (Object o1, Object o2)	{
				
		if (o1 == null | o2 == null)	return 0.0;
		int match = 0, total = 0;

		JSONArray a1 = (JSONArray) o1;
		JSONArray a2 = (JSONArray) o2;
		total = a1.size() + a2.size();

		for (int i = 0; i < a1.size(); i ++)	{
			for (int j = 0; j < a2.size(); j++)	{
				if (a1.get(i).toString().equals(a2.get(j).toString()))	{
					match++;
					total--;
				}
			}
		}
		return (double)(match)/(double)total;
	}

	double compareSoftware (Object o1, Object o2)	{
		
		if (o1 == null | o2 == null)	return 0.0;
		int match = 0, total;
		
		JSONArray a1 = (JSONArray) o1;
		JSONArray a2 = (JSONArray) o2;
		String s1;
		String s2;
		String[] array1;
		String[] array2;
		total = a1.size() + a2.size();

		for (int i = 0; i < a1.size(); i++)	{
			s1 = a1.get(i).toString();
			for (int j = 0; j < a2.size(); j++)	{
				s2 = a2.get(j).toString();
				if (compareSoftwareHelper(s1, s2))	{
					match++;
					total--;
				}
			}
		}		
		
		return (double)match/(double)total;
	}
		
	boolean compareSoftwareHelper	(String s1, String s2)	{

		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		String[] a = s1.split(":");
		String[] b = s2.split(" ");
		boolean equals = false;

		int j = 0;
		for (int i = 2; i < a.length; i++)	{	
			if (a[i].toString().equals(b[j].toString()))	{	 
				if (i == a.length - 1)	equals = true;
			}
			else if (++j == b.length)	break;
			else break;
		}	
		
		return equals;
	}
				
	void addToMatchTree (double similarityScore, JSONObject objectOne, JSONObject objectTwo)	{
	
		JSONObject[] pair = {objectOne, objectTwo};
		matchTree.put(similarityScore, pair);
	}
	
	//printing "limit" elements starting in descending order		
	void printMatchTree(int limit)	{
		
		JSONObject[] array = new JSONObject[2];
		
		int count = 0;
		Iterator it = matchTree.entrySet().iterator();
		while (it.hasNext())	{
			Map.Entry entry = (Map.Entry) it.next();
			array = (JSONObject[]) entry.getValue();
			System.out.println("similarityScore  =  " + entry.getKey());
			System.out.println("--objectOne-->   " + array[0]);
			System.out.println("--objectTwo-->   " + array[1]);
			System.out.println(count);
			System.out.println();
			count++;
			if (count == limit) break;
		}
	}
}
