package alignmentStudy;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.*;

public class NVDParse {

	public NVDParse (String[] args)	{
		try {
			FileReader reader = new FileReader (args[0]);
			JSONParser parser = new JSONParser ();
			JSONArray array = (JSONArray)parser.parse(reader);
			Maps maps = new Maps(array.size()); 	//size of JSON array
			Iterator it = array.iterator();
			while (it.hasNext())	{
				JSONObject object = (JSONObject) it.next();
				for (Object key : object.keySet())	{	
					maps.addToMap(key.toString(), object.get(key));
				}
			}
		//	maps.printAllMaps();
		//	maps.printMap("cvssDate");
		//	maps.printPercentage();
		//	maps.getMap("id");
			maps.calculateEntropy();
			maps.printEntropy();
		} catch (FileNotFoundException e)	{
			e.printStackTrace();
		} catch (IOException e)		{
			e.printStackTrace();
		} catch (ParseException e)	{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)	{
		new NVDParse (args);
	}
}
