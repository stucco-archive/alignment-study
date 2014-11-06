package alignmentStudy;

import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
				
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
		
		for (int i = 0; i < arrayOne.size(); i++)	{
			for (int j = 0; j < arrayTwo.size(); j++)	{
				ObjectsSimilarity os = new ObjectsSimilarity();
				os.objectOne = (JSONObject) arrayOne.get(i);
				os.objectTwo = (JSONObject) arrayTwo.get(j);
				os.descriptionSimilarity = compareDescription (os.objectOne.get("description"), os.objectTwo.get("description"));	 
				os.softwareSimilarity = compareSoftware (os.objectOne.get("vulnerableSoftware"), os.objectTwo.get("Vulnerable"));
				os.idAndClassSimilarity = 0.0;	//compareIdAndClass(i, j);
				os.publTimeSimilarity = compareDate (os.objectOne.get("publishedDate"), os.objectTwo.get("publishedDate"));	
				os.modifTimeSimilarity = compareDate (os.objectOne.get("modifiedDate"), os.objectTwo.get("modifiedDate"));
				os.referenceSimilarity = compareReferences (os.objectOne.get("references"), os.objectTwo.get("references"));
				
				System.out.println(os.descriptionSimilarity);
	
				addToMatchTree(matchTree, os);	
			}
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
