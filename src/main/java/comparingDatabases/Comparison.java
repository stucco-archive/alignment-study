package alignmentStudy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.*;
import java.net.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import net.sf.json.JSONSerializer;
import net.sf.json.JSON;

public class Comparison {

	private  DateFormat df;
	private  RemoveStopWords rsw;

	public Comparison()	{

		df = new DateFormat();
		rsw = new RemoveStopWords("StopWords.txt");
	}

	//returns 0.0 if time is not specifyed in both objects
	public double compareDate (Object dateOne, Object dateTwo)	{

		if (dateOne == null | dateTwo == null)	return 0.0;

		long timeOne = 0, timeTwo = 0;
											
		timeOne = df.formatNVDDate(dateOne.toString());
		timeTwo = df.formatBugtraqDate(dateTwo.toString());
										
		if (timeOne == timeTwo) return 1.0;
		else	return 1.0/(double)Math.abs(timeOne - timeTwo);
	}
											
	//return is between 0.0 (nothing in common) and 1.0				
	public double compareReferences (Object referenceListOne, Object referenceListTwo)	{
				
		if (referenceListOne == null | referenceListTwo == null)	return 0.0;
		
		int match = 0, total = 0;
												
<<<<<<< HEAD
	//	JSONArray a1 = (JSONArray) referenceListOne;
	//	JSONArray a2 = (JSONArray) referenceListTwo;
	
=======
>>>>>>> 69610843cdf68b21897b21c02a02af52bfb08757
		ArrayList a1 = (ArrayList) referenceListOne;
		ArrayList a2 = (ArrayList) referenceListTwo;

		total = a1.size() + a2.size();

		for (int i = 0; i < a1.size(); i ++)	{
			for (int j = 0; j < a2.size(); j++)	{
				if (a1.get(i).toString().equals(a2.get(j).toString()))	{
					match++;
					total--;
				}
			}
		}

		return (double)(match)/(double)total;
	}

	public double compareSoftware (Object softwareListOne, Object softwareListTwo)	{	
						
		if (softwareListOne == null | softwareListTwo == null) return 0.0;
	
		SmithWaterman sw = new SmithWaterman();
												
		return sw.smithWatermanScore(softwareListOne.toString(), softwareListTwo.toString());
	}

						
	public double compareSoftwareExactly (Object softwareListOne, Object softwareListTwo)	{	

		if (softwareListOne == null | softwareListTwo == null)	return 0.0;
		int match = 0, total;
		
		JSONArray a1 = (JSONArray) softwareListOne;
		JSONArray a2 = (JSONArray) softwareListTwo;
		String s1;
		String s2;
		String[] array1;
		String[] array2;
		total = a1.size() + a2.size();

		for (int i = 0; i < a1.size(); i++)	{
			s1 = a1.get(i).toString();
			for (int j = 0; j < a2.size(); j++)	{
				s2 = a2.get(j).toString();
				if (compareSoftwareHelper(s1, s2))	{
					match++;
					total--;
				}
			}
		}		
		
		return (double)match/(double)total;
	}
		
	boolean compareSoftwareHelper	(String s1, String s2)	{

		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		String[] a = s1.split(":");
		String[] b = s2.split(" ");
		boolean equals = false;

		int j = 0;
		for (int i = 2; i < a.length; i++)	{	
			if (a[i].toString().equals(b[j].toString()))	{	 
				if (i == a.length - 1)	equals = true;
			}
			else if (++j == b.length)	break;
			else break;
		}	
		
		return equals;
	}


	//removing unnecessary chars from words
	public String removeChars(String text)	{

		text = text.replaceAll("\\W", "");	//removes nonalphabetic chars
		
		return text;
	}
				
	public boolean isAStopWord (String word)	{
	
		return rsw.containsString(word);
	}
															
	public void addToMatchTree (TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree, ObjectsSimilarity os)	{

		double 	similarityScore = os.descriptionSimilarity + os.publTimeSimilarity + os.modifTimeSimilarity + os.referenceSimilarity + os.softwareSimilarity;
		
		if (matchTree.get(similarityScore) == null)	{
			ArrayList<ObjectsSimilarity> l = new ArrayList<ObjectsSimilarity>();
			l.add(os);
			matchTree.put(similarityScore, l);
		}
		else	{
			ArrayList<ObjectsSimilarity> l = new ArrayList<ObjectsSimilarity>(matchTree.get(similarityScore));
			l.add(os);
			matchTree.put(similarityScore, l);
		}
	}

	//printing "limit" elements starting in descending order		
	public void printMatchTree(TreeMap<Double, ArrayList<ObjectsSimilarity>> matchTree,  int limit, String outFile)	{

		limit = matchTree.size();

		try {
                	FileWriter fw = new FileWriter(outFile);	//true is for appending	
			BufferedWriter bw = new BufferedWriter(fw);

			JSONSerializer js = new JSONSerializer();
			JSONObject[] array = new JSONObject[2];

			boolean done = false;
			int count = 0;
			Iterator it = matchTree.entrySet().iterator();
			while (it.hasNext() && !done)	{
				Map.Entry entry = (Map.Entry) it.next();
				ArrayList<ObjectsSimilarity> l = new ArrayList<ObjectsSimilarity>();
				l = (ArrayList<ObjectsSimilarity>) entry.getValue();
				for (int i = 0; i < l.size(); i++)	{
					bw.write("WHIRL: \n");
					bw.write("---------------  \n");
					bw.write("similarityScore  =  " + entry.getKey() + "\n");
					bw.write("descriptionScore = " + l.get(i).descriptionSimilarity + "\n");
					bw.write("publTimeSimilarity = " + l.get(i).publTimeSimilarity + "\n");
					bw.write("modifTimeSimilarity = " + l.get(i).modifTimeSimilarity + "\n");
					bw.write("referenceSimilarity = " + l.get(i).referenceSimilarity + "\n");
					bw.write("softwareSimilarity = " + l.get(i).softwareSimilarity + "\n");
					bw.write("idAndClassSimilarity = " + l.get(i).idAndClassSimilarity + "\n");
					JSON jsonOne = js.toJSON(l.get(i).objectOne.toString());
					bw.write("- objectOne: \n" + jsonOne.toString(2) + "\n");
					JSON jsonTwo = js.toJSON(l.get(i).objectTwo.toString());
					bw.write("- objectTwo: \n " + jsonTwo.toString(2) + "\n");
					count++;
					bw.write("\n****************************************************************\n\n");
					if (count == limit)	{
						done = true;
					 	break;	
					}
				}
			}	
			bw.close();
		} catch (IOException e)	{
			e.printStackTrace();
		}
	}
}
