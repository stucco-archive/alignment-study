package alignmentStudy;

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

public class CompareWithWHIRL	{

	private JSONObject objectOne;
	private JSONObject objectTwo;
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private RemoveStopWords rsw;
	private RemoveStopWords glossary;
	private DateTimeFormat dtf;
	private Map<Integer, Map<String, Integer>>  mapOne; 	//size of JSON array
	private Map<Integer, Map<String, Integer>>  mapTwo; 	//size of JSON array
	private Map<Integer, Map<String, Integer>>  idMap; 	//size of JSON array
	private Map<Integer, Map<String, Integer>>  classMap; 	//size of JSON array
	private Map<String, Integer> allWords;
	private Map<String, Integer> idAndClassAllWords;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;
	private char[] extraChars;	//used to remove extra chars from text and trim extra space

	public CompareWithWHIRL (JSONArray arrayOne, JSONArray arrayTwo)	{

		objectOne = new JSONObject ();
		objectTwo = new JSONObject ();
		mapOne = new HashMap<Integer, Map<String, Integer>>(); 	
		mapTwo = new HashMap<Integer, Map<String, Integer>>(); 	
		idMap = new HashMap<Integer, Map<String, Integer>>(); 	
		classMap = new HashMap<Integer, Map<String, Integer>>(); 	
		matchTree = new TreeMap<Double, ArrayList<ObjectsSimilarity>>(Collections.reverseOrder());
		dtf = new DateTimeFormat();
		rsw = new RemoveStopWords("StopWords.txt");
	//	glossary = new RemoveStopWords("glossary.txt");
		allWords = new HashMap<String, Integer>();				
		idAndClassAllWords = new HashMap<String, Integer>();				
		extraChars = ",.:;[]?!1234567890<>[]{}()*&^%$#@/+=_-''".toCharArray();	//array of chars to be removed, could be modified
		this.arrayOne = arrayOne;
		this.arrayTwo = arrayTwo;

		compare();
	}

	void compare()	{

		storeArrayOneIntoMaps();	//arrayOne into mapOne and allWords
		storeArrayTwoIntoMaps();	//arrayTwo into mapTwo and allWords
		compareDatabases();		//comparing descripiton
	//	printMatchTree(10, "myTable.txt");		//print first 5 best matches
	}

	//creating map for cosine similarity and tf-idf
	void storeArrayOneIntoMaps()	{
		for (int i = 0; i < arrayOne.size(); i++)	{
			Map <String, Integer> map = new HashMap<String, Integer>();
			Map <String, Integer> tempMap = new HashMap<String, Integer>();
			objectOne = (JSONObject)arrayOne.get(i);
			if (objectOne.get("description") != null)	{
				stemObjectAndAddToAllWordsMap (objectOne.get("description").toString(), map);
			}
			if (objectOne.get("vulnerableSoftware") != null)	{
				JSONArray array = (JSONArray) objectOne.get("vulnerableSoftware");
				String string = cpeToString(array.get(0).toString());
				for (int j = 1; j < array.size(); j++)	{
					string = string + " " + cpeToString(array.get(j).toString());  
				}
				stemObjectAndAddToAllWordsMap (string, map);
			}
			mapOne.put(i, map);
		}
	}

	//creating map for cosine similarity and tf-idf
	void storeArrayTwoIntoMaps()	{
		for (int i = 0; i < arrayTwo.size(); i++)	{
			Map <String, Integer> map = new HashMap<String, Integer>();
			Map <String, Integer> tempMap = new HashMap<String, Integer>();
			objectTwo = (JSONObject)arrayTwo.get(i);
			if (objectTwo.get("description") != null)	{
				stemObjectAndAddToAllWordsMap (objectTwo.get("description").toString(), map);
			}
			if (objectOne.get("Vulnerable") != null)	{
				JSONArray array = (JSONArray) objectOne.get("Vulnerable");
				String string = cpeToString(array.get(0).toString());
				for (int j = 1; j < array.size(); j++)	{
					string = string + " " + cpeToString(array.get(j).toString());  
				}
				stemObjectAndAddToAllWordsMap (string, map);
			}
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
			str[j] = removeChars(str[j]);	//removes extra chars and trims extra space;
			ps.add(str[j].toCharArray(), str[j].length());		
			ps.stem();
			key = ps.toString();
                                if (map.get(key) == null)	map.put(key, 1);
				else	{
					count =  map.get(key);
					count++;
					map.put(key, count); 
				}
				if (allWords.get(key) == null)	allWords.put(key, 1);
				else if (!done){

					count = allWords.get(key);
					count++;
					allWords.put(key, count);
					done = true;
				}
                       
                }
	}

	void  stemObjectAndAddToIdAndClassAllWordsMap (String s, Map<String, Integer> map)	{

		PorterStemmer ps = new PorterStemmer();
		String[] str = s.split(" ");
		String substring, key;
		boolean done = false;
		int count;

		for (int j = 0; j < str.length; j++)  {
			if (rsw.containsString(str[j])) continue;	//removing stop words
			str[j] = str[j].toLowerCase();    
			str[j] = removeChars(str[j]);	//removes extra chars and trims extra space;
			ps.add(str[j].toCharArray(), str[j].length());
			ps.stem();
			key = ps.toString();

                        for (int i = 0; i <= key.length() - 2; i++)  {
                                if (map.get(substring = key.substring(i, i + 2)) == null)	map.put(substring, 1);
				else	{
					count =  map.get(substring);
					count++;
					map.put(substring, count); 
				}
				if (idAndClassAllWords.get(substring) == null)	idAndClassAllWords.put(substring, 1);
				else if (!done){

					count = idAndClassAllWords.get(substring);
					count++;
					idAndClassAllWords.put(substring, count);
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

	void addingAllWords()	{
		
		for (Object s : allWords.keySet())	{
			if (glossary.containsString(s.toString()))
				allWords.put(s.toString(), 1);
		}
	
		
	}

	//function traversing arrays and comparing corresponding elements
	void compareDatabases()	{

	//	addingAllWords();
	
		double bestSimilarityScore = 0.0, similarityScore, descrSimilarity, publTimeSimilarity, modifTimeSimilarity, referenceSimilarity, softwareSimilarity;
		int indexOne = -1, indexTwo = -1;		

		for (int i = 0; i < arrayOne.size(); i++)	{
			objectOne = (JSONObject) arrayOne.get(i);
			for (int j = 0; j < arrayTwo.size(); j++)	{
				objectTwo = (JSONObject) arrayTwo.get(j);
				ObjectsSimilarity os = new ObjectsSimilarity();
				os.objectOne = objectOne;
				os.objectTwo = objectTwo;		
				os.descriptionSimilarity = compareDescriptions(i, j);	 //sending index of corresponding objects to compare
				os.referenceSimilarity = compareReferences (objectOne.get("references"), objectTwo.get("references"));
				os.softwareSimilarity = compareSoftware (objectOne.get("vulnerableSoftware"), objectTwo.get("Vulnerable"));
				os.idAndClassSimilarity = compareIdAndClass(i, j);
				os.publTimeSimilarity = compareTime (i, j, "publishedDate");	//specifying type of time to compare
				os.modifTimeSimilarity = compareTime (i, j, "modifiedDate");
				similarityScore = os.descriptionSimilarity + 
							os.publTimeSimilarity + 
							os.modifTimeSimilarity + 
							os.referenceSimilarity + 
							os.softwareSimilarity + 
							os.idAndClassSimilarity;
				addToMatchTree(similarityScore, os);	
			}

		}
	}

	//compares descriptions using WHIRL or cosine similarity
	double compareDescriptions(int i, int j)	{

		if (mapOne.get(i) == null | mapTwo.get(j) == null)	return 0.0;

		Map<String, Integer> iMap = new HashMap<String, Integer>();
		Map<String, Integer> jMap = new HashMap<String, Integer>();
		double 	similarity, vDinominator = 0.0, uDinominator = 0.0, numerator = 0.0; 
		double[] v = new double [allWords.size()];
		double[] u = new double [allWords.size()];

		iMap = mapOne.get(i);
		jMap = mapTwo.get(j);
		calculateV(iMap, v);
		calculateU (jMap, u);
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
	void calculateV (Map <String, Integer> map, double[] array)	{

		double tf, idf;
		int x = 0;

		for (String s : allWords.keySet())	{
			if (map.get(s) == null)	array[x] = 0.0;
			else	{
				tf = (double)map.get(s);
				idf = (double)mapOne.size()/(double)allWords.get(s);	 
				array[x] = Math.log(tf + 1.0) * Math.log(idf);	
			}
			x++;					
		}
	}

	//helper function for compareDescriptions
	void calculateU (Map <String, Integer> map, double[] array)	{

		double tf, idf;
		int x = 0;

		for (String s : allWords.keySet())	{
			if (map.get(s) == null)	array[x] = 0.0;
			else	{
				tf = (double)map.get(s);
				idf = (double)mapTwo.size()/(double)allWords.get(s);	 
				array[x] = Math.log(tf + 1.0) * Math.log(idf);	
			}
			x++;
		}					
	}

	//compares descriptions using WHIRL or cosine similarity
	double compareIdAndClass(int i, int j)	{

		if (idMap.get(i) == null | classMap.get(j) == null)	return 0.0;
		Map<String, Integer> iMap = new HashMap<String, Integer>();
		Map<String, Integer> jMap = new HashMap<String, Integer>();
		double 	similarity, vDinominator = 0.0, uDinominator = 0.0, numerator = 0.0; 
		double[] v = new double [idAndClassAllWords.size()];
		double[] u = new double [idAndClassAllWords.size()];


		iMap = idMap.get(i);
		jMap = classMap.get(j);
		calculateUForIdAndClass(iMap, v);
		calculateVForIdAndClass (jMap, u);
		for (int k = 0; k < idAndClassAllWords.size(); k++)	{
			numerator = numerator + (v[k] * u[k]);
			vDinominator = vDinominator + (v[k] * v[k]); 
			uDinominator = uDinominator + (u[k] * u[k]);					
		}
		if (vDinominator == 0.0 | uDinominator == 0.0)	similarity = 0.0;
		else	similarity = numerator / (Math.sqrt(vDinominator) * Math.sqrt(uDinominator));

		return similarity;
	}

	//helper function for compareDescriptions
	void calculateVForIdAndClass (Map <String, Integer> map, double[] array)	{

		double tf, idf;
		int x = 0;

		for (String s : idAndClassAllWords.keySet())	{
			if (map.get(s) == null)	array[x] = 0.0;
			else	{
				tf = (double)map.get(s);
				idf = (double)idMap.size()/(double)idAndClassAllWords.get(s);	 
				array[x] = Math.log(tf + 1.0) * Math.log(idf);	
			}
			x++;
		}					
	}

	//helper function for compareDescriptions
	void calculateUForIdAndClass (Map <String, Integer> map, double[] array)	{

		double tf, idf;
		int x = 0;

		for (String s : idAndClassAllWords.keySet())	{
			if (map.get(s) == null)	array[x] = 0.0;
			else	{
				tf = (double)map.get(s);
				idf = (double)classMap.size()/(double)idAndClassAllWords.get(s);	 
				array[x] = Math.log(tf + 1.0) * Math.log(idf);	
			}
			x++;
		}					
	}

	//returns 0.0 if time is not specifyed in both objects
	double compareTime (int i, int j, String typeOfTime)	{

		objectOne = (JSONObject)arrayOne.get(i);
		objectTwo = (JSONObject)arrayTwo.get(j);
		if (objectOne.get(typeOfTime) == null | objectTwo.get(typeOfTime) == null |
			objectOne.get("source") == null && objectTwo.get("source").toString() == null) return 0.0;

		long timeOne = 0, timeTwo = 0;

		if (objectOne.get("source").toString().equals("NVD"))
			timeOne = dtf.formatNVDDateTime(objectOne.get(typeOfTime).toString());
		if (objectOne.get("source").toString().equals("bugtraq"))
			timeOne = dtf.formatBugtraqDateTime(objectOne.get(typeOfTime).toString());
		if (objectTwo.get("source").toString().equals("NVD"))
			timeTwo = dtf.formatNVDDateTime(objectTwo.get(typeOfTime).toString());
		if (objectTwo.get("source").toString().equals("bugtraq"))
			timeTwo = dtf.formatBugtraqDateTime(objectTwo.get(typeOfTime).toString());

		if (timeOne == timeTwo) return 1.0;
		else	return 1.0/(double)Math.abs(timeOne - timeTwo);
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
			s1.replaceAll("_", " ");
			for (int j = 0; j < a2.size(); j++)	{
				s2 = a2.get(j).toString();
				s2.replaceAll("_", " ");
				if (compareSoftwareHelper(s1, s2))	
					match++;}
			}
 
		return (double)match/(double)(total - match);
	}

	boolean compareSoftwareHelper	(String s1, String s2)	{

		s1 = cpeToString(s1.toLowerCase());
		s2 = cpeToString(s2.toLowerCase());		

		return s1.equals(s2);
	}


	String cpeToString (String str)	{

		String[] array;
		String newStr;

		if (str.length() > 7 && str.charAt(0) == 'c' && str.charAt(1) == 'p' && str.charAt(2) == 'e')   {
 				str = str.substring(7, str.length());
 			array = str.split(":");
 		}
 		else    array = str.split(" ");

		newStr = array[0];
		for (int i = 1; i < array.length; i++)	{
			if (array[i] != " ")	newStr = newStr + " " + array[i];
		}

		return newStr;
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

		limit = matchTree.size();

		try {
                	FileWriter fw = new FileWriter(outFile);	//true is for appending	
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
					bw.write("WHIRL: \n");
					bw.write("---------------  \n");
					bw.write("similarityScore  =  " + entry.getKey() + "\n");
					bw.write("descriptionScore = " + l.get(i).descriptionSimilarity + "\n");
					bw.write("publTimeSimilarity = " + l.get(i).publTimeSimilarity + "\n");
					bw.write("modifTimeSimilarity = " + l.get(i).modifTimeSimilarity + "\n");
					bw.write("referenceSimilarity = " + l.get(i).referenceSimilarity + "\n");
					bw.write("softwareSimilarity = " + l.get(i).softwareSimilarity + "\n");
					bw.write("idAndClassSimilarity = " + l.get(i).idAndClassSimilarity + "\n");
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
