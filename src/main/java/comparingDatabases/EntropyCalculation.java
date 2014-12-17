package alignmentStudy;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.*;

import org.json.*;

import net.sf.json.JSONSerializer;
import net.sf.json.JSON;

import org.apache.commons.io.IOUtils;

public class EntropyCalculation {
	
	private BufferedWriter bw;
	private FileReader reader;
	private FileWriter writer;
	private JSONObject object;
	private JSONArray array;
	private JSONSerializer jsonSerializer;
	private JSON json;
	private JSONObject entropyObject;	
	private Map <String, Map<Object, Integer>> objectsMap;
	private Map <Object, Integer> tempInnerMap;
	private Map <String, Integer> countFields;
	private Map <String, Double> entropyMap;
	private Iterator it;
	private String database;
	private double entropy;
	private int countKey, nullCount, arraySize;

	public EntropyCalculation (JSONArray inputArray)	{
		
		this.array = inputArray;
		objectsMap = new HashMap<String, Map<Object, Integer>>();
		countFields = new HashMap<String, Integer>();
		entropyMap = new HashMap<String, Double>();		
		entropyObject = new JSONObject();	
		arraySize = array.length();

		traceArrayAndAddToMap();
		calculateEntropy();
		createEntropyJSONObject();
	//	printEntropy();
	}	
	
	void traceArrayAndAddToMap()	{
		
		try {
			for (int i = 0; i < arraySize ; i++)	{
				object = array.getJSONObject(i);
				Iterator iter = object.keys();
				while (iter.hasNext())	{
					String key = (String) iter.next();
					Object value = object.get(key);
					addToMap (key, value);
				}	
			}
		} catch(JSONException e)	{
			e.printStackTrace();
		}
	}

	//constructing a map with objects fields as a keys, and counting map as a values
	void addToMap (String key, Object value)	{
		tempInnerMap = new HashMap<Object, Integer>();
		if (objectsMap.containsKey(key))	{	//adding new value to existing key
			countKey = countFields.get(key);
			countKey++;				
			countFields.put(key, countKey);
			tempInnerMap = objectsMap.get(key);
			if (tempInnerMap.containsKey(value))	{
				int count = tempInnerMap.get(value);
				count++;
				tempInnerMap.put(value, count);
				objectsMap.put(key, tempInnerMap);
			} 
			else {
				tempInnerMap.put(value, 1);
				objectsMap.put(key, tempInnerMap);
			}
		} 
		else {
			tempInnerMap.put(value, 1);	//adding new key to outer map
			objectsMap.put(key, tempInnerMap);
			countFields.put(key, 1);
		}
	}	

	void calculateEntropy ()	{
		for (String key: objectsMap.keySet())	{
			tempInnerMap = new HashMap<Object, Integer>();
			entropy = 0.0;
			nullCount = 0;
			tempInnerMap = objectsMap.get(key);
			for (Object k : tempInnerMap.keySet())	{
				if (tempInnerMap.get(k) == 0) 
					continue;
				entropy = entropy + ((double)tempInnerMap.get(k)/arraySize) * Math.log((double)tempInnerMap.get(k)/arraySize);
			}
			//adding entropy from missing fields
			if ((nullCount = arraySize - countFields.get(key)) == 0)	{
				entropyMap.put(key, new Double (entropy * -1));
			} 
			else	{
				entropy = entropy + ((double)nullCount/arraySize) * Math.log((double)nullCount/arraySize);
				entropyMap.put(key, new Double (entropy * -1));
			}
		}
	}

	void createEntropyJSONObject()	{
		
		try {	
			for (String key : entropyMap.keySet())	{
				entropyObject.put(key, entropyMap.get(key));
			}
		} catch(JSONException e)	{
			e.printStackTrace();
		}
	}

	JSONObject getEntropy ()	{
		
		return entropyObject;
	}

	void printEntropy ()	{

		for (String key : entropyMap.keySet())	{
			System.out.println(key + " = " + entropyMap.get(key));
		}
	}

	//printing entire map of the maps
	void printAllMaps ()	{
		for (String key : objectsMap.keySet())	{
			tempInnerMap = new HashMap<Object, Integer>();
			System.out.println(key + ":");
			tempInnerMap = objectsMap.get(key);	
			for (Object k : tempInnerMap.keySet())	{
				System.out.println(k + " = " + tempInnerMap.get(k));
			}
			System.out.println();
		}

	}

	//printing map with specific key
	void printMap (String key)	{
		tempInnerMap = new HashMap<Object, Integer>();
		tempInnerMap = objectsMap.get(key);	
		System.out.println(tempInnerMap.size());
		for (Object k : tempInnerMap.keySet())	{
			System.out.println(k + " = " + tempInnerMap.get(k));
		}
	}

	//printing keys and how many times they were appearing in document
	void printCount ()	{
		for (String key : countFields.keySet())	
			System.out.println(key + " = " + countFields.get(key));
	}
	
	//printing all maps and percentage of every value
	void printPercentage()	{
		for (String key : objectsMap.keySet())	{
			tempInnerMap = new HashMap<Object, Integer>();
			System.out.println(key + ":");
			tempInnerMap = objectsMap.get(key);	
			countKey = countFields.get(key);
			for (Object k : tempInnerMap.keySet())	{
				System.out.print(k + " = " + tempInnerMap.get(k));
				if (tempInnerMap.get(k) != 0)
					System.out.println(" = " + (double)tempInnerMap.get(k)/countKey * 100 + "%");
				else	
					System.out.println(" = " + 0);
			}	
			System.out.println();
		}
	}
}
