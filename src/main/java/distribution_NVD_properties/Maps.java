package alignmentStudy;

import java.util.*;

public class Maps {
	private Map <String, Map<Object, Integer>> objectsMap;
	private Map <Object, Integer> tempInnerMap;
	private Map <String, Integer> countFields;
	private Iterator it;
	int countKey;

	public Maps ()	{

		objectsMap = new HashMap<String, Map<Object, Integer>>(18);
		countFields = new HashMap<String, Integer>();
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

	void printMap (String key)	{
		tempInnerMap = new HashMap<Object, Integer>();
		tempInnerMap = objectsMap.get(key);	
		for (Object k : tempInnerMap.keySet())	{
			System.out.println(k + " = " + tempInnerMap.get(k));
		}
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
