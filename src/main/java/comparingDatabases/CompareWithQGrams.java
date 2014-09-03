package alignmentStudy;

//program is comparing two documents: NVD and Bugtraq to find most similar objectsd
//based on description, published time, modified time, and references, vulnerable software
//each property worth from 0.0 (have nothing in common) to 1.0 (the same)
//times are compared using difference in times represented in unix integer
//descriptions are compared with QGrams algorithm

import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class CompareWithQGrams extends Comparison implements ComparisonMethod {
				
	private JSONArray arrayOne;
	private JSONArray arrayTwo;
	private TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree;
	private final int qNumber;
	
	public CompareWithQGrams (JSONArray arrayOne, JSONArray arrayTwo, int qNumber)	{

		matchTree = new TreeMap<Double, ArrayList<ObjectsSimilarity>>(Collections.reverseOrder());
		this.arrayOne = arrayOne;
		this.arrayTwo = arrayTwo;
		this.qNumber = qNumber;
			
		compareDatabases();		//comparing descripiton
	//	printMatchTree(5, "comparisonTable.txt");		//print first 5 best matches
	}
	
	//function traversing arrays and comparing corresponding elements
	public void compareDatabases()	{

		for (int i = 0; i < arrayOne.size(); i++)	{
			for (int j = 0; j < arrayTwo.size(); j++)	{
				ObjectsSimilarity os = new ObjectsSimilarity();
				os.objectOne = (JSONObject) arrayOne.get(i);
				os.objectTwo = (JSONObject) arrayTwo.get(j);
															
				os.descriptionSimilarity = compareDescription (os.objectOne.get("description"), os.objectTwo.get("description"));
				os.publTimeSimilarity = compareDate (os.objectOne.get("publishedDate"), os.objectTwo.get("publishedDate"));	
				os.modifTimeSimilarity = compareDate (os.objectOne.get("modifiedDate"), os.objectTwo.get("modifiedDate"));
				os.referenceSimilarity = compareReferences (os.objectOne.get("references"), os.objectTwo.get("references"));
				os.softwareSimilarity = compareSoftware (os.objectOne.get("vulnerableSoftware"), os.objectTwo.get("Vulnerable"));
										
				addToMatchTree(matchTree, os);
			}
		}
	}
	
	//compares descriptions using WHIRL or cosine similarity
	public double compareDescription (Object descriptionOne, Object descriptionTwo)	{

		if (descriptionOne == null | descriptionTwo == null) return 0.0;

		QGrams qg = new QGrams();

		return qg.qGramsDistance(descriptionOne.toString(), descriptionTwo.toString(), qNumber);
	}

	public TreeMap<Double, ArrayList<ObjectsSimilarity>> getMatchTree()	{
	
		return matchTree;
	}

	public String getName () {

		return "QGrams";
	}
}
