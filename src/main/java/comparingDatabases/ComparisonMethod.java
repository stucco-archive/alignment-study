package alignmentStudy;

import java.util.TreeMap;
import java.util.ArrayList;

public interface ComparisonMethod {
	
	void compareDatabases();
	double compareDescription (Object descriptionOne, Object descriptionTwo);
	double compareDate (Object dateOne, Object dateTwo);
	double compareSoftware (Object softwareListOne, Object sofwareListTwo);
	double compareReferences (Object referencesListOne, Object referencesListTwo);
	String removeChars(String text);
	void addToMatchTree (TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree, ObjectsSimilarity os);
	void printMatchTree(TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree,  int limit, String outFile); 
	TreeMap<Double, ArrayList<ObjectsSimilarity>> getMatchTree();
	String getName();
}
	
