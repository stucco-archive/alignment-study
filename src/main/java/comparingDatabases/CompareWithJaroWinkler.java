package alignmentStudy;

//program is comparing two documents: NVD and Bugtraq to find most similar objectsd
//based on description, published time, modified time, and references, vulnerable software
//each property worth from 0.0 (have nothing in common) to 1.0 (the same)
//times are compared using difference in times represented in unix integer
//descriptions are compared with Jaro Winkler algorithm
import java.util.*;

import org.json.*;
					
public class CompareWithJaroWinkler extends Comparison implements ComparisonMethod {
	
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;

	public CompareWithJaroWinkler (JSONArray arrayOne, JSONArray arrayTwo)	{
		
		matchTree = new TreeMap<Double, ArrayList<ObjectsSimilarity>>(Collections.reverseOrder());
		this.arrayOne = arrayOne;
		this.arrayTwo = arrayTwo;
			
		compareDatabases();		//comparing descripiton
	//	printMatchTree(5, "comparisonTable.txt");		//print first 5 best matches
	}

	//function traversing arrays and comparing corresponding elements
	public void compareDatabases()	{
		
		try {
			for (int i = 0; i < arrayOne.length(); i++)	{
				for (int j = 0; j < arrayTwo.length(); j++)	{
					ObjectsSimilarity os = new ObjectsSimilarity();
					os.objectOne = arrayOne.getJSONObject(i);
					os.objectTwo = arrayTwo.getJSONObject(j);
															
					if (os.objectOne.has("description") && os.objectTwo.has("description"))	{
						os.descriptionSimilarity = compareDescription(os.objectOne.get("description"), os.objectTwo.get("description"));	 //sending index of corresponding objects to compare
					}
					else os.descriptionSimilarity = 0.0;

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
		} catch (JSONException e)	{
			e.printStackTrace();
		}
	}
	
	//compares descriptions using WHIRL or cosine similarity
	public double compareDescription (Object descriptionOne, Object descriptionTwo)	{

		if (descriptionOne == null | descriptionTwo == null) return 0.0;

		JaroWinkler jw = new JaroWinkler();

		return jw.getJaroWinklerDistance(descriptionOne.toString(), descriptionTwo.toString());
	}

	public TreeMap<Double, ArrayList<ObjectsSimilarity>> getMatchTree()	{
		
		return matchTree;
	}

	public String getName() {

		return "Jaro-Winkler";
	}
}
