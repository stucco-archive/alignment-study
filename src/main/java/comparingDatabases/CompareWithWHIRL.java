package alignmentStudy;

import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CompareWithWHIRL extends Comparison implements ComparisonMethod {

	private WHIRL whirlForDescription;
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;

	public CompareWithWHIRL (JSONArray arrayOne, JSONArray arrayTwo)	{

		whirlForDescription = new WHIRL();
		matchTree = new TreeMap<Double, ArrayList<ObjectsSimilarity>>(Collections.reverseOrder());
		
		this.arrayOne = arrayOne;
		this.arrayTwo = arrayTwo;

		compareDatabases();
	}
	
	//function traversing arrays and comparing corresponding elements
	public void compareDatabases()	{
		
		setWhirlMapsForDescriptions();
		
		for (int i = 0; i < arrayOne.size(); i++)	{
			for (int j = 0; j < arrayTwo.size(); j++)	{
				ObjectsSimilarity os = new ObjectsSimilarity();
				os.objectOne = (JSONObject) arrayOne.get(i);
				os.objectTwo = (JSONObject) arrayTwo.get(j);
				os.descriptionSimilarity = compareDescription(i, j);	 //sending index of corresponding objects to compare
				os.softwareSimilarity = compareSoftware (os.objectOne.get("vulnerableSoftware"), os.objectTwo.get("Vulnerable"));
				os.idAndClassSimilarity = 0.0;	//compareIdAndClass(i, j);
				os.publTimeSimilarity = compareDate (os.objectOne.get("publishedDate"), os.objectTwo.get("publishedDate"));	
				os.modifTimeSimilarity = compareDate (os.objectOne.get("modifiedDate"), os.objectTwo.get("modifiedDate"));
				os.referenceSimilarity = compareReferences (os.objectOne.get("references"), os.objectTwo.get("references"));
				
				addToMatchTree(matchTree, os);	
			}
		}
	}
	
	void setWhirlMapsForDescriptions()	{
	
		Map<Integer, String> textFieldOne = new HashMap<Integer, String>(); 	
		Map<Integer, String> textFieldTwo = new HashMap<Integer, String>(); 	
		
		for (int i = 0; i < arrayOne.size(); i++)	{
			if (((JSONObject)arrayOne.get(i)).get("description") != null)
			textFieldOne.put(i, ((JSONObject)arrayOne.get(i)).get("description").toString());
		}
		for (int i = 0; i < arrayTwo.size(); i++)	{
			if (((JSONObject)arrayTwo.get(i)).get("description") != null)
			textFieldTwo.put(i, ((JSONObject)arrayTwo.get(i)).get("description").toString());
			
		}
		whirlForDescription.setTextMaps(textFieldOne, textFieldTwo);
	}

	public double compareDescription (Object i, Object j)	{
									
		return	whirlForDescription.getSimilarityScore(new Integer (i.toString()), new Integer (j.toString()));	 //sending index of corresponding objects to compare
	}

	public TreeMap<Double, ArrayList<ObjectsSimilarity>> getMatchTree()	{
		
		return matchTree;
	}

	public String getName() {
		
		return "WHIRL";
	}
}
