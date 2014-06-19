import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AnalyzeNVDObject {

	private Maps maps;
	private Iterator it;

	public AnalyzeNVDObject (Maps maps)	{
		this.maps = maps;
	}

	void parseJsonObjectAndAddToMaps (JSONObject object)	{
			
		Iterator it;

		maps.addToMap("id",object.get("_id"));
		maps.addToMap("type",object.get("_type"));
		maps.addToMap("vertexType", object.get("vertexType"));
		maps.addToMap("sourse", object.get("source"));
		maps.addToMap("description", object.get("description"));
		maps.addToMap("publishedDate", object.get("publishedDate"));
		maps.addToMap("modifiedDate", object.get("modifiedDate"));
		maps.addToMap("cweNumber", object.get("cweNumber"));
		maps.addToMap("cvssScore", object.get("cvssScore"));
		maps.addToMap("accessVector", object.get("accessVector"));
		maps.addToMap("accessComplexity", object.get("accessComplexity"));
		maps.addToMap("accessAuthentication",object.get("accessAuthentication"));
		maps.addToMap("confidentialityImpact", object.get("confidentialityImpact"));
		maps.addToMap("intefrityImpact", object.get("integrityImpact"));
		maps.addToMap("availabilityImpact", object.get("availabilityImpact"));
		maps.addToMap("cvssDate", object.get("cvssDate"));
		maps.addToMap("references", object.get("references"));
	//	it = references.iterator();
	//	while (it.hasNext())	{
	//		System.out.print(it.next() + " ");
	//	}
	//	System.out.println();
		maps.addToMap("vulnerableSoftware", object.get("vulnerableSoftware"));
	//	it = vulnerableSoftware.iterator();
	//	while (it.hasNext())	{
	//		System.out.print(it.next() + " ");
	//	}
	//	System.out.println();
	}
}
