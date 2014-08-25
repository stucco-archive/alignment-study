package alignmentStudy;

public class QGrams	{
	
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

                s1 = new String (str1);
                s2 = new String (str2);
		
		stringConstruction();	
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
	
	//calculating distance using q-grams
	void calculateDistance()	{
		
		for (int i = 0; i <= length1 - q; i++)	{
			substringCount++;
			for (int j = 0; j <= length2 - q; j++)	{
				if (s1.substring(i, i + q).equals(s2.substring(j, j + q)))	{
					overlapSubstringCount++;
					break;
				}
			}
		}
		
		substringCount = (length1 - q + 1) + (length2 - q + 1);
		distance = (double)overlapSubstringCount/(substringCount - overlapSubstringCount);	
	}
}










