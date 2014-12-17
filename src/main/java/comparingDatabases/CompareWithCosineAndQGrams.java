package alignmentStudy;

import java.util.*;

import org.json.*;
				
public class CompareWithCosineAndQGrams extends Comparison implements ComparisonMethod {

//	private CosineAndQGrams caqgForDescription;
//	private CosineAndQGrams caqgForSoftware;
	private CosineAndQGrams caqg;
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;

	public CompareWithCosineAndQGrams (JSONArray arrayOne, JSONArray arrayTwo)	{

		caqg = new CosineAndQGrams();
		matchTree = new TreeMap<Double, ArrayList<ObjectsSimilarity>>(Collections.reverseOrder());
		
		this.arrayOne = arrayOne;
		this.arrayTwo = arrayTwo;

		compareDatabases();
	}
	
	//function traversing arrays and comparing corresponding elements
	public void compareDatabases()	{
		
		try {

			for (int i = 0; i < arrayOne.length(); i++)	{
				for (int j = 0; j < arrayTwo.length(); j++)	{
					ObjectsSimilarity os = new ObjectsSimilarity();
					os.objectOne = arrayOne.getJSONObject(i);
					os.objectTwo = arrayTwo.getJSONObject(j);
																									
					if (os.objectOne.has("description") && os.objectTwo.has("description")) {
						os.descriptionSimilarity = compareDescription (os.objectOne.get("description"), os.objectTwo.get("description"));	 
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
	
	public double compareDescription (Object descriptionOne, Object descriptionTwo)	{
		
		if (descriptionOne == null || descriptionTwo == null)	return 0.0;	
	
		//sending index of corresponding objects to compare their description fields
																			
		return caqg.getSimilarityScore(descriptionOne.toString(), descriptionTwo.toString());	 
	}
	
	public TreeMap<Double, ArrayList<ObjectsSimilarity>> getMatchTree()	{
		return matchTree;
	}

	public String getName () {

		return "Cosine Similarity with QGrams";
	}
}
