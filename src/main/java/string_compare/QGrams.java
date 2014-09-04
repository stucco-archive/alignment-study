package alignmentStudy;

import java.util.*;

public class QGrams	{
	
	private Set<String> setOne;
	private Set<String> setTwo;
//	private Set<String> total;
	private String s1;
	private String s2;
	private int length1;	
	private int length2;	
	private int q;
	private double distance;
	private int substringCount;
	private int overlapSubstringCount;
		


	//functions to initialize private variables & call distance computing functions
	// returning distance uaing Jaccord method Normalization method, where 0 means full overla and 1 is no overlap 
	double qGramsDistance ( String str1, String str2, int qNumber)	{
		
		if (str1.equals(str2))  return 1.0;       //checking if strings are the same
                if ((length1 = str1.length()) == 0)     return 0.0; 
                if ((length2 = str2.length()) == 0)     return 0.0; 
  		if ((q = qNumber) == 0 || q > length1 || q > length2)	return -1.0;	//q is too big | too small

		setOne = new HashSet<String>();
		setTwo = new HashSet<String>();
        //        total = new HashSet<String>();
		s1 = new String (str1);
                s2 = new String (str2);
		
		stringConstruction();	
		setSets();
		calculateDistance();	//1.0 if s1=s2, 0.0 otherwise
		
		return distance;
	}
		
	//adding prefix & postfix to the strings
	void stringConstruction()	{

		String symbol = "#";
		
		for (int i = 1; i < q - 1; i++)	symbol = symbol + "#";
		s1 = symbol + s1 + symbol;
		s2 = symbol + s2 + symbol;
		length1 += (q-1) * 2;		
		length2 += (q-1) * 2;		
	}
	
	void setSets()	{

		for (int i = 0; i <= length1 - q; i++)	{
			setOne.add(s1.substring(i, i + q));
		//	total.add(s1.substring(i, i + q));
		}
													
		for (int j = 0; j <= length2 - q; j++)	{
			setTwo.add(s2.substring(j, j + q));
		//	total.add(s2.substring(j, j + q));
		}
	}

	//calculating distance using q-grams
	void calculateDistance()	{
		
		for (String substringOne : setOne)	{
			for (String substringTwo : setTwo)	{
				if (substringOne.equals(substringTwo))
					overlapSubstringCount++;
			}
		}

		substringCount = (length1 - q) + (length2 - q) + 2 - overlapSubstringCount; 
			//= total.size();
		distance = (double)overlapSubstringCount/(substringCount - overlapSubstringCount);	
	}
}










