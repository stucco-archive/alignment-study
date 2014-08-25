package alignmentStudy;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CosineWithQGrams	{
	private FileReader readerOne;
	private FileReader readerTwo;
	private JSONParser parserOne;
	private JSONParser parserTwo;
	private JSONObject objectOne;
	private JSONObject objectTwo;
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private Map<Integer, Map<String, Integer>>  mapOne; 	//size of JSON array
	private Map<Integer, Map<String, Integer>>  mapTwo; 	//size of JSON array
	private Map<String, Integer> allWords;
	private Map <String, Integer> iMap;
	private Map <String, Integer> jMap;
	private JSONCompare distance;
	private RemoveStopWords rsw;
	private DateTimeFormat dtf;

	public CosineWithQGrams(String[] args)	{

			System.out.println("HERE");
		try {
			readerOne = new FileReader (args[0]);
			readerTwo = new FileReader (args[1]);
			parserOne = new JSONParser ();
			parserTwo = new JSONParser ();
			objectOne = new JSONObject ();
			objectTwo = new JSONObject ();
			arrayOne = (JSONArray)parserOne.parse(readerOne);
			arrayTwo = (JSONArray)parserTwo.parse(readerTwo);
			mapOne = new HashMap<Integer, Map<String, Integer>>(); 	
			mapTwo = new HashMap<Integer, Map<String, Integer>>(); 	
			iMap = new HashMap<String, Integer>();
			jMap = new HashMap<String, Integer>();
			distance = new JSONCompare ();
			dtf = new DateTimeFormat();
			rsw = new RemoveStopWords("StopWords.txt");
			allWords = new HashMap<String, Integer>();				
			
			storeArraysInMaps();
			compareObjects();
		} catch (FileNotFoundException e)	{
			e.printStackTrace();
		} catch (IOException e)		{
			e.printStackTrace();
		} catch (ParseException e)	{
			e.printStackTrace();
		}		
		
	}

	void storeArraysInMaps ()	{
		for (int i = 0; i < arrayOne.size(); i++)	{
			Map <String, Integer> map = new HashMap<String, Integer>();
			objectOne = (JSONObject)arrayOne.get(i);
			if (objectOne.get("description") != null)
				stemObjectAndAddToAllWordsMap (objectOne.get("description").toString(), map);
			mapOne.put(i, map);
		}
		
		for (int i = 0; i < arrayTwo.size(); i++)	{
			Map <String, Integer> map = new HashMap<String, Integer>();
			objectTwo = (JSONObject)arrayTwo.get(i);
			stemObjectAndAddToAllWordsMap (objectTwo.get("description").toString(), map);
			if (objectTwo.get("description") != null)
				stemObjectAndAddToAllWordsMap (objectTwo.get("description").toString(), map);
			mapTwo.put(i, map);
		}

	}

	void  stemObjectAndAddToAllWordsMap (String s, Map<String, Integer> map)	{
		
		PorterStemmer ps = new PorterStemmer();
		String[] str = s.split(" ");
		String substring, key;
		boolean done = false;
		int count;
	
		for (int j = 0; j < str.length; j++)  {
			if (rsw.containsString(str[j])) continue;	//removing stop words
                        str[j] = str[j].toLowerCase();    
                      	str[j] = str[j].replaceAll ("[.,:;'!?'['']'(){}+-1234567890'\n']", "");     
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
				if (allWords.get(substring) == null)	{
					allWords.put(substring, 1);
				}
				else if (!done){
					count = allWords.get(substring);
					count++;
					allWords.put(substring, count);
					done = true;
				}
                        }
                }
	}

	void compareObjects()	{
	
		double 	tf, idf, similarity = 0.0, vDinominator = 0.0, uDinominator = 0.0, dinominator = 0.0, numerator = 0.0, 
			bestScore = 0.0, descriptionScore = 0.0, publishedDate = 0.0, modifiedDate = 0.0, referencesSimilarity, softwareSimilarity = 0.0; 		

		for (int i = 0; i < mapOne.size(); i++)	{
			double[] v = new double [allWords.size()];
			iMap = mapOne.get(i);
			calculateUOrV(iMap, v);
			for (int j = 0; j < mapTwo.size(); j++)	{
				double[] u = new double [allWords.size()];
				jMap = mapTwo.get(j);
				numerator = 0.0;
				vDinominator = 0.0;
				uDinominator = 0.0;
				similarity = 0.0;
				calculateUOrV (jMap, u);
				for (int k = 0; k < allWords.size(); k++)	{
					numerator = numerator + (v[k] * u[k]);
					vDinominator = vDinominator + (v[k] * v[k]); 
					uDinominator = uDinominator + (u[k] * u[k]);					
				}
				if (vDinominator == 0.0 | uDinominator == 0.0)	similarity = 0.0;
				else	similarity = numerator / (Math.sqrt(vDinominator) * Math.sqrt(uDinominator));
				publishedDate = compareTime (i, j, "publishedDate");
				modifiedDate = compareTime (i, j, "modifiedDate");
				objectOne = (JSONObject)arrayOne.get(i);	
				objectTwo = (JSONObject)arrayTwo.get(j);	
				referencesSimilarity = cosineSimilarityForReferences (objectOne.get("references"), objectTwo.get("references"));
				softwareSimilarity = cosineSimilarityForSoftware (objectOne.get("vulnerableSoftware"), objectTwo.get("Vulnerable"));
				System.out.println(similarity);
			}
			iMap.clear();
			jMap.clear();
		}
	}

	double cosineSimilarityForSoftware (Object o1, Object o2)	{
		
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
				if (compareSoftware(s1, s2))	{
					match++;
					total--;
				}
			}
		}		
		
		return (double)match/(double)total;
	}
		
	boolean compareSoftware	(String s1, String s2)	{

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

	double cosineSimilarityForReferences (Object o1, Object o2)	{
				
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

	//	System.out.println(match + " " + total);
		return (double)(match)/(double)total;
	}

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

	double compareTime (int i, int j, String typeOfTime)	{
		
		long timeOne, timeTwo;

		objectOne = (JSONObject)arrayOne.get(i);
		objectTwo = (JSONObject)arrayTwo.get(j);
		if (objectOne.get(typeOfTime) != null && objectTwo.get(typeOfTime) != null)	{
			timeOne = dtf.formatNVDDateTime(objectOne.get(typeOfTime).toString());
			timeTwo = dtf.formatBugtraqDateTime(objectTwo.get(typeOfTime).toString());
			return Math.abs(timeOne - timeTwo);
		}
		else return -1;
	}

	public static void main (String[] args)	{
		
		System.out.println("HERE");
		new CosineWithQGrams (args);
	}	
}
