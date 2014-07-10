//function return distance = 1.0 if absolute match, else 0 <= distance < 1.0
//reference: 	1. 	An Adaptive String Comparator for Record Linkage
//			William E. Yancey
//			Statistical Research Division U.S. Bureau of the Census Washington D.C. 20233
//		2.	Wikipedia
//			http://en.wikipedia.org/wiki/Jaro-Winkler_distance
public class JaroWinkler {
	
	private String s1;
	private String s2;
	private int length1;
	private int length2;
	private double distance;
	private int m;		//match
	private int t;		//transposition
	private int l;		//longest common prefix, but no longer than 4
	private int range;

	double getJaroWinklerDistance (String str1, String str2)	{
		
		if (str1.equals(str2))	return 1.0;	//if strigns are the same
		if ((length1 = str1.length()) == 0)	return 0.0;	//check for empty strigs
		if ((length2 = str2.length()) == 0)	return 0.0;
		
		//initialization
		m = 0;
		t = 0;
		l = 0;
		s1 = new String(str1);
		s2 = new String(str2);
		range = (int)Math.floor(Math.max(length1, length2)/2) - 1;	//some resourses take min 
		
		//setting the first string to be the longest
		if (length2 > length1)	{
			String tempS1 = s1;
			int tempLength = length1;
			s1 = s2;
			length1 = length2;
			s2 = tempS1;
			length2 = tempLength;
		}		
		setMatchAndTransposition();
		setDistance();

		return distance;
	}	

	void setMatchAndTransposition ()	{
		
		boolean [] match1 = new boolean[length1];
		boolean [] match2 = new boolean[length2];

		for (int i = 0; i < length1; i++)	{
			for (int j = i - range; j < i && j < length2; j++)	{	
				if (j >= 0)	{
					if (s1.charAt(i) == s2.charAt(j) && match1[i] == false && match2[j] == false)	{
						m++;
						match1[i] = true;
						match2[j] = true;
					}
				}
			}
			for (int j = i; j < length2 && j < i + range; j++)	{
				if (s1.charAt(i) == s2.charAt(j) && match1[i] == false && match2[j] == false)	{
					m++;
					match1[i] = true;	//bool arrays to mark matches
					match2[j] = true;
				}
			}	
		}

		//calculating transposition using bool arrays
		int x = 0, y = 0;
		while (true)	{
			while (x < length1 && match1[x] == false)	x++;
			while (y < length2 && match2[y] == false) 	y++;
			if (x == length1 || y == length2)	break;
			if (s1.charAt(x) != s2.charAt(y))	t++;
			x++;
			y++;
		}
		t = t/2;
	}
	
	void setDistance ()	{
		if(m == 0)	{
			distance = 0.0;
		} else	{
			distance = ((double)m/length1 + (double)m/length2 + (double)(m-t)/m)/3.0;	
			for (int i = 0; i < 4 && i < length1 && i < length2; i++)	{
				if (s1.charAt(i) == s2.charAt(i))	l++;
				else	break;
			}
			
			distance = distance + (l * 0.1 * (1 - distance));
		}
	}

}
