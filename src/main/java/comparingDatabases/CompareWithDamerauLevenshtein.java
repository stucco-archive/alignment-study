package alignmentStudy;

//program is comparing two documents: NVD and Bugtraq to find most similar objectsd
//based on description, published time, modified time, and references, vulnerable software
//each property worth from 0.0 (have nothing in common) to 1.0 (the same)
//times are compared using difference in times represented in unix integer
//descriptions are compared using Damerau Levenshtein algorithm

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

import org.apache.commons.io.IOUtils;

public class CompareWithDamerauLevenshtein	{
	
	private JSONObject objectOne;
	private JSONObject objectTwo;
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private RemoveStopWords rsw;
	private DateTimeFormat dtf;
	private Map<Integer, Map<String, Integer>>  mapOne; 	//size of JSON array
	private Map<Integer, Map<String, Integer>>  mapTwo; 	//size of JSON array
	private Map<String, Integer> allWords;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;
	private char[] extraChars;	//used to remove extra chars from text and trim extra space

	public CompareWithDamerauLevenshtein (JSONArray arrayOne, JSONArray arrayTwo)	{
	
		System.out.println("DamerauLevenshtein");	
		objectOne = new JSONObject ();
		objectTwo = new JSONObject ();
		matchTree = new TreeMap<Double, ArrayList<ObjectsSimilarity>>(Collections.reverseOrder());
		dtf = new DateTimeFormat();
		rsw = new RemoveStopWords("StopWords.txt");
		this.arrayOne = arrayOne;
		this.arrayTwo = arrayTwo;

		compare();
	}

	void compare()	{
			
		compareDatabases();		//comparing descripiton
	//	printMatchTree(5, "comparisonTable.txt");		//print first 10 best matches
	}

	//function traversing arrays and comparing corresponding elements
	void compareDatabases()	{
		
		double similarityScore, descrSimilarity, publTimeSimilarity, modifTimeSimilarity, referenceSimilarity, softwareSimilarity;

		for (int i = 0; i < arrayOne.size(); i++)	{
			objectOne = (JSONObject) arrayOne.get(i);
			for (int j = 0; j < arrayTwo.size(); j++)	{
				ObjectsSimilarity os = new ObjectsSimilarity();
				objectTwo = (JSONObject) arrayTwo.get(j);
				os.objectOne = objectOne;
				os.objectTwo = objectTwo;
				os.descriptionSimilarity = compareDescriptions(objectOne.get("description"), objectTwo.get("description"));
				os.publTimeSimilarity = compareTime (i, j, "publishedDate");	//specifying type of time to compare
				os.modifTimeSimilarity = compareTime (i, j, "modifiedDate");
				os.referenceSimilarity = compareReferences (objectOne.get("references"), objectTwo.get("references"));
				os.softwareSimilarity = compareSoftware (objectOne.get("vulnerableSoftware"), objectTwo.get("Vulnerable"));
				similarityScore = os.descriptionSimilarity + os.publTimeSimilarity + os.modifTimeSimilarity + os.referenceSimilarity + os.softwareSimilarity;
				addToMatchTree (similarityScore, os);
			}
		}
	}	
	
	//compares descriptions using WHIRL or cosine similarity
	double compareDescriptions(Object o1, Object o2)	{

		if (o1 == null | o2 == null) return 0.0;

		DamerauLevenshteinDistance dld = new DamerauLevenshteinDistance();
	
		return dld.damerauLevenshteinDistance(o1.toString(), o2.toString());	
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
			//	if (compareSoftwareHelper(s1, s2))	{
				if (s1.equals(s2))	{
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
				
	void addToMatchTree (double similarityScore, ObjectsSimilarity os)	{
	
		if (matchTree.get(similarityScore) == null)	{
			ArrayList<ObjectsSimilarity> l = new ArrayList<ObjectsSimilarity>();
			l.add(os);
			matchTree.put(similarityScore, l);
		}
		else	{
			ArrayList<ObjectsSimilarity> l = new ArrayList<ObjectsSimilarity>(matchTree.get(similarityScore));
			l.add(os);
			matchTree.put(similarityScore, l);
		}
	}
	
	//printing "limit" elements starting in descending order		
	void printMatchTree(int limit, String outFile)	{
		
		try {
                	FileWriter fw = new FileWriter(outFile, true);	//true is for appending	
			BufferedWriter bw = new BufferedWriter(fw);

			JSONSerializer js = new JSONSerializer();
			JSONObject[] array = new JSONObject[2];
			
			boolean done = false;
			int count = 0;
			Iterator it = matchTree.entrySet().iterator();
			while (it.hasNext() && !done)	{
				Map.Entry entry = (Map.Entry) it.next();
				ArrayList<ObjectsSimilarity> l = new ArrayList<ObjectsSimilarity>();
				l = (ArrayList<ObjectsSimilarity>) entry.getValue();
				for (int i = 0; i < l.size(); i++)	{
					bw.write("Damerau Levenshtein: \n");
					bw.write("---------------  \n");
					bw.write("similarityScore  =  " + entry.getKey() + "\n");
					bw.write("descriptionScore = " + l.get(i).descriptionSimilarity + "\n");
					bw.write("publTimeSimilarity = " + l.get(i).publTimeSimilarity + "\n");
					bw.write("modifTimeSimilarity = " + l.get(i).modifTimeSimilarity + "\n");
					bw.write("referenceSimilarity = " + l.get(i).referenceSimilarity + "\n");
					bw.write("softwareSimilarity = " + l.get(i).softwareSimilarity + "\n");
					JSON jsonOne = js.toJSON(l.get(i).objectOne.toString());
					bw.write("- objectOne: \n" + jsonOne.toString(2) + "\n");
					JSON jsonTwo = js.toJSON(l.get(i).objectTwo.toString());
					bw.write("- objectTwo: \n " + jsonTwo.toString(2) + "\n");
					count++;
					bw.write("\n****************************************************************\n\n");
					if (count == limit)	{
						done = true; 	
						break;
					}	
				}
			}	
			bw.close();
		} catch (IOException e)	{
			e.printStackTrace();
		}
	}

	TreeMap<Double, ArrayList<ObjectsSimilarity>> getMatchTree()	{
		return matchTree;
	}

}
