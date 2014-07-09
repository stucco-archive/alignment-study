//program is comparing two documents: NVD and Bugtraq to find most similar objects
//based on description, published time, modified time, and references, vulnerable software
//each property worth from 0.0 (have nothing in common) to 1.0 (the same)
//times are compared using difference in times represented in unix integer
//descriptions are compared using cosine similarity with qgrams (2)
//WHIRL could be used to compare descriptions by uncommenting lines in calculateUOrV function 

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CompareNvdAndBugtraqDatabases	{
	private FileReader readerOne;
	private FileReader readerTwo;
	private JSONParser parserOne;
	private JSONParser parserTwo;
	private JSONObject objectOne;
	private JSONObject objectTwo;
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private RemoveStopWords rsw;
	private DateTimeFormat dtf;
	private Map<Integer, Map<String, Integer>>  mapOne; 	//size of JSON array
	private Map<Integer, Map<String, Integer>>  mapTwo; 	//size of JSON array
	private Map<String, Integer> allWords;
	private TreeMap matchTree;
	private char[] extraChars;	//used to remove extra chars from text and trim extra space

	public CompareNvdAndBugtraqDatabases(String[] args)	{

		try {
			readerOne = new FileReader (args[0]);
			readerTwo = new FileReader (args[1]);
			parserOne = new JSONParser ();
			parserTwo = new JSONParser ();
			objectOne = new JSONObject ();
			objectTwo = new JSONObject ();
			arrayOne = (JSONArray)parserOne.parse(readerOne); //JSON array from NVD database
			arrayTwo = (JSONArray)parserTwo.parse(readerTwo); //JSON array from bugtraq database
			mapOne = new HashMap<Integer, Map<String, Integer>>(); 	
			mapTwo = new HashMap<Integer, Map<String, Integer>>(); 	
			matchTree = new TreeMap(Collections.reverseOrder());
			dtf = new DateTimeFormat();
			rsw = new RemoveStopWords("StopWords.txt");
			allWords = new HashMap<String, Integer>();				
			extraChars = ",.:;[]?!1234567890<>[]{}()*&^%$#@/+=_-''".toCharArray();	//array of chars to be removed, could be modified
			
			storeNVDArrayIntoMaps();	//arrayOne into mapOne and allWords
			storeBugtraqArrayIntoMaps();	//arrayTwo into mapTwo and allWords
			compareDatabases();		//comparing descripiton
			printMatchTree(10);	//print first 10 best matches
		} catch (FileNotFoundException e)	{
			e.printStackTrace();
		} catch (IOException e)		{
			e.printStackTrace();
		} catch (ParseException e)	{
			e.printStackTrace();
		}		
	}

	//creating map for cosine similarity and tf-idf
	void storeNVDArrayIntoMaps()	{
		for (int i = 0; i < arrayOne.size(); i++)	{
			Map <String, Integer> map = new HashMap<String, Integer>();
			objectOne = (JSONObject)arrayOne.get(i);
			if (objectOne.get("description") != null)
				stemObjectAndAddToAllWordsMap (objectOne.get("description").toString(), map);
			mapOne.put(i, map);
		}
	}

	//creating map for cosine similarity and tf-idf
	void storeBugtraqArrayIntoMaps()	{
		for (int i = 0; i < arrayTwo.size(); i++)	{
			Map <String, Integer> map = new HashMap<String, Integer>();
			objectTwo = (JSONObject)arrayTwo.get(i);
			if (objectTwo.get("description") != null)
				stemObjectAndAddToAllWordsMap (objectTwo.get("description").toString(), map);
			mapTwo.put(i, map);
		}
	}

	//removing suffixes from all words 
	void  stemObjectAndAddToAllWordsMap (String s, Map<String, Integer> map)	{
		
		PorterStemmer ps = new PorterStemmer();
		String[] str = s.split(" ");
		String substring, key;
		boolean done = false;
		int count;
	
		for (int j = 0; j < str.length; j++)  {
			if (rsw.containsString(str[j])) continue;	//removing stop words
			str[j] = str[j].toLowerCase();    
		//	str[j] = str[j].replaceAll ("[.,:;'!?'['']'(){}+-1234567890'\n']", "");     
			str[j] = removeChars(str[j]);	//removes extra chars and trims extra space;
			ps.add(str[j].toCharArray(), str[j].length());
			ps.stem();
			key = "#" + ps.toString() + "#";
			
                        for (int i = 0; i <= key.length() - 2; i++)  {
                                if (map.get(substring = key.substring(i, i + 2)) == null)	map.put(substring, 1);
				else	{
					count =  map.get(substring);
					count++;
					map.put(substring, count); 
				}
				if (allWords.get(substring) == null)	allWords.put(substring, 1);
				else if (!done){
					
					count = allWords.get(substring);
					count++;
					allWords.put(substring, count);
					done = true;
				}
                        }
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

	//function traversing arrays and comparing corresponding elements
	void compareDatabases()	{
		
		double bestSimilarityScore = 0.0, similarityScore, descrSimilarity, publTimeSimilarity, modifTimeSimilarity, referenceSimilarity, softwareSimilarity;
		int indexOne = -1, indexTwo = -1;		

		for (int i = 0; i < arrayOne.size(); i++)	{
			objectOne = (JSONObject) arrayOne.get(i);
			for (int j = 0; j < arrayTwo.size(); j++)	{
				objectTwo = (JSONObject) arrayTwo.get(j);
				descrSimilarity = compareDescriptions(i, j);	 //sending index of corresponding objects to compare
				publTimeSimilarity = compareTime (i, j, "publishedDate");	//specifying type of time to compare
				modifTimeSimilarity = compareTime (i, j, "modifiedDate");
				referenceSimilarity = compareReferences (objectOne.get("references"), objectTwo.get("references"));
				softwareSimilarity = compareSoftware (objectOne.get("vulnerableSoftware"), objectTwo.get("Vulnerable"));
				similarityScore = descrSimilarity + publTimeSimilarity + modifTimeSimilarity + referenceSimilarity + softwareSimilarity;
				addToMatchTree (similarityScore, objectOne, objectTwo);
				if (similarityScore > bestSimilarityScore)	{
					bestSimilarityScore = similarityScore;
					indexOne = i;
					indexTwo = j;
				}
			}
		}
		//	if (timeSimilarity > 0.7)	{
			//	objectOne = (JSONObject) arrayOne.get(indexOne);
			//	objectTwo = (JSONObject) arrayTwo.get(indexTwo);
			//	System.out.println("similarity = " + bestSimilarityScore);
			//	System.out.println(" ---> " + objectOne.get("publishedDate").toString());
			//	System.out.println(" ---> " + objectTwo.get("publishedDate").toString());
			//	System.out.println(" ---> " + objectOne.toString());
			//	System.out.println(" ---> " + objectTwo.toString());
			//	System.out.println();	
		//	}
		
	}
	
	//compares descriptions using WHIRL or cosine similarity
	double compareDescriptions(int i, int j)	{
	
		Map<String, Integer> iMap = new HashMap<String, Integer>();
		Map<String, Integer> jMap = new HashMap<String, Integer>();
		double 	similarity, vDinominator = 0.0, uDinominator = 0.0, numerator = 0.0; 
		double[] v = new double [allWords.size()];
		double[] u = new double [allWords.size()];

		
		iMap = mapOne.get(i);
		jMap = mapTwo.get(j);
		calculateUOrV(iMap, v);
		calculateUOrV (jMap, u);
		for (int k = 0; k < allWords.size(); k++)	{
			numerator = numerator + (v[k] * u[k]);
			vDinominator = vDinominator + (v[k] * v[k]); 
			uDinominator = uDinominator + (u[k] * u[k]);					
		}
		if (vDinominator == 0.0 | uDinominator == 0.0)	similarity = 0.0;
		else	similarity = numerator / (Math.sqrt(vDinominator) * Math.sqrt(uDinominator));
		
		return similarity;
	}
	
	//helper function for compareDescriptions
	void calculateUOrV (Map <String, Integer> map, double[] array)	{
		
		int x = 0;
		for (String s : allWords.keySet())	{
			if (map.get(s) == null)	array[x] = 0.0;
			else	{
			//	tf = (double)map.get(s);	//tf-idf
			//	idf = (double)mapTwo.size()/(double)allWords.get(s);	//tf-idf 
			//	array[x] = Math.log(tf + 1.0) * Math.log(idf);	//tf-idf
				array[x] = (double)map.get(s);	//cosine similarity
			}
			x++;
		}					
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

	public static void main (String[] args)	{
		
		new CompareNvdAndBugtraqDatabases (args);
	}	
}
