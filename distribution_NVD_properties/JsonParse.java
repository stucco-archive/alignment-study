import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonParse {

	public JsonParse (String[] args)	{
		try {
			FileReader reader = new FileReader ("NVD.json");
			JSONParser parser = new JSONParser ();
			JSONArray array = (JSONArray)parser.parse(reader);
		
			Iterator it = array.iterator();
			while (it.hasNext())	{
				parseJsonObject ((JSONObject) it.next());
			}
		} catch (FileNotFoundException e)	{
			e.printStackTrace();
		} catch (IOException e)		{
			e.printStackTrace();
		} catch (ParseException e)	{
			e.printStackTrace();
		}

	}

	void parseJsonObject (JSONObject object)	{
			
		Iterator it;
	
		String id = (String) object.get("_id");
		System.out.println("id is: " + id);	
				
		String type = (String) object.get("_type");
		System.out.println("type is: " + type);
				
		String vertexType = (String) object.get("vertexType");
		System.out.println("vertexType is : " + vertexType);
	
		String sourse = (String) object.get("source");
		System.out.println("sourse is: " + sourse);
				
		String description = (String) object.get("description");
		System.out.println("description is: " + description);
				
		String publishedDate = (String) object.get("publishedDate");
		System.out.println("publishedDate is: " + publishedDate);

		String modifiedDate = (String) object.get("modifiedDate");
		System.out.println("modifiedDate is: " + modifiedDate);

		String cweNumber = (String) object.get("cweNumber");
		System.out.println("cweNumber is: " + cweNumber);

		long cvssScore = (long) object.get("cvssScore");
		System.out.println("cvssScore is: " + cvssScore);
				
		String accessVector = (String) object.get("accessVector");
		System.out.println("accessVector is: " + accessVector);

		String accessComplexity = (String) object.get("accessComplexity");
		System.out.println("accessComplexity is: " + accessComplexity);
				
		String accessAuthentication = (String) object.get("accessAuthentication");
		System.out.println("accessAuthentication is: " + accessAuthentication);
				
		String confidentialityImpact = (String) object.get("confidentialityImpact");
		System.out.println("confidentialityImpact is: " + confidentialityImpact);
				
		String integrityImpact = (String) object.get("integrityImpact");
		System.out.println("integrityImpact is: " + integrityImpact);
				
		String availabilityImpact = (String) object.get("availabilityImpact");
		System.out.println("availabilityImpact is: " + availabilityImpact);
				
		String cvssDate = (String) object.get("cvssDate");
		System.out.println("cvssDate is: " + cvssDate);
			
		JSONArray references = (JSONArray) object.get("references");
		it = references.iterator();
		while (it.hasNext())	{
			System.out.print(it.next() + " ");
		}
		System.out.println();

		JSONArray vulnerableSoftware = (JSONArray) object.get("vulnerableSoftware");
		it = vulnerableSoftware.iterator();
		while (it.hasNext())	{
			System.out.print(it.next() + " ");
		}
		System.out.println();
	}

	public static void main(String[] args)	{
		new JsonParse (args);
	}
}
