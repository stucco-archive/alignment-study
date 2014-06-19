import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NVDParse {

	public NVDParse (String[] args)	{
		try {
			FileReader reader = new FileReader (args[0]);
			JSONParser parser = new JSONParser ();
			JSONArray array = (JSONArray)parser.parse(reader);
			Maps maps = new Maps();
			AnalyzeNVDObject ano = new AnalyzeNVDObject(maps);
			Iterator it = array.iterator();
			
			while (it.hasNext())	{
				ano.parseJsonObjectAndAddToMaps ((JSONObject) it.next());
			}
			maps.printAllMaps();
			maps.printMap("id");
			maps.printPercentage();
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
