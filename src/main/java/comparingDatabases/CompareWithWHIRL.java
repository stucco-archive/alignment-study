package alignmentStudy;

import java.util.*;

import org.json.*;

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
	public void compareDatabases(){
		
		try {							
			setWhirlMapsForDescriptions();

			for (int i = 0; i < arrayOne.length(); i++)	{
				for (int j = 0; j < arrayTwo.length(); j++)	{
					ObjectsSimilarity os = new ObjectsSimilarity();
					os.objectOne = arrayOne.getJSONObject(i);
					os.objectTwo = arrayTwo.getJSONObject(j);
					
					
					if (os.objectOne.has("_id") && os.objectTwo.has("CVE")) {
						String idOne = os.objectOne.getString("_id");					
						String idTwo = os.objectTwo.getString("CVE");					
						if (os.objectOne.has("description") && os.objectTwo.has("description")) {
							os.descriptionSimilarity = compareDescription(idOne, idTwo);	 //sending index of corresponding objects to compare
						}
						else os.descriptionSimilarity = 0.0;
					}
					if (os.objectOne.has("vulnerableSoftware") && os.objectTwo.has("Vulnerable"))	{
						os.softwareSimilarity = compareSoftware (os.objectOne.get("vulnerableSoftware"), os.objectTwo.get("Vulnerable"));
					}
					else os.softwareSimilarity = 0.0;
					os.idAndClassSimilarity = 0.0;	//compareIdAndClass(i, j);
				
					if (os.objectOne.has("publishedDate") && os.objectTwo.has("publishedDate"))	{
						os.publTimeSimilarity = compareDate (os.objectOne.get("publishedDate"), os.objectTwo.get("publishedDate"));	
					}
					else os.publTimeSimilarity = 0.0;

					if (os.objectOne.has("modifiedDate") && os.objectTwo.has("modifiedDate"))	{
						os.modifTimeSimilarity = compareDate (os.objectOne.get("modifiedDate"), os.objectTwo.get("modifiedDate"));
					}
					else os.modifTimeSimilarity = 0.0;
			
					if (os.objectOne.has("references") && os.objectTwo.has("references"))	{
						os.referenceSimilarity = compareReferences (os.objectOne.get("references"), os.objectTwo.get("references"));
					}
					else os.referenceSimilarity = 0.0;
			
					addToMatchTree(matchTree, os);	
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
							
	void setWhirlMapsForDescriptions() throws JSONException {
	
		Map<String, String> textFieldOne = new HashMap<String, String>(); 	
		Map<String, String> textFieldTwo = new HashMap<String, String>(); 	
		
		for (int i = 0; i < arrayOne.length(); i++)	{
			if ((arrayOne.getJSONObject(i)).get("description") != null)
			textFieldOne.put(arrayOne.getJSONObject(i).getString("_id"), (arrayOne.getJSONObject(i)).getString("description"));
		}
		for (int i = 0; i < arrayTwo.length(); i++)	{
			if ((arrayTwo.getJSONObject(i)).get("description") != null)
			textFieldTwo.put(arrayTwo.getJSONObject(i).getString("CVE"), (arrayTwo.getJSONObject(i)).getString("description"));
			
		}
		whirlForDescription.setTextMaps(textFieldOne, textFieldTwo);
	}

	public double compareDescription (Object i, Object j)	{
									
		return	whirlForDescription.getSimilarityScore(i.toString(), j.toString());	 //sending index of corresponding objects to compare
	}

	public TreeMap<Double, ArrayList<ObjectsSimilarity>> getMatchTree()	{
		
		return matchTree;
	}

	public String getName() {
		
		return "WHIRL";
	}
}
