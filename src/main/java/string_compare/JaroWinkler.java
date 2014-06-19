package alignmentStudy;

//function return distance = 1.0 if absolute match, else 0 <= distance < 1.0
public class JaroWinkler {
	
	private String s1;
	private String s2;
	private int length1;
	private int length2;
	private int m;		//match
	private int t;		//transposition
	private double distance;
	private int range;

	double getDistance (String str1, String str2)	{
		
		if (str1.equals(str2))	return 1.0;	//if strigns are the same
		if ((length1 = str1.length()) == 0)	return 0.0;	//check for empty strigs
		if ((length2 = str2.length()) == 0)	return 0.0;
		
		//initialization
		m = 0;
		t = 0;
		s1 = new String(str1);
		s2 = new String(str2);
		range = (int)Math.floor(Math.min(length1, length2)/2) - 1;
		
		//setting the first string to be the longest
		if (length1 > length2)	{
			String temp1 = s1;
			int temp2 = length1;

			s1 = s2;
			s2 = temp1;
			length1 = length2;
			length2 = temp2;
		}		

		setMatchAndTransposition();
		setDistance();

		return distance;
	}	

	void setMatchAndTransposition ()	{
		
		boolean [] match1 = new boolean[length1];
		boolean [] match2 = new boolean[length2];
		
		for (int i = 0; i < length1; i++)	{
			//loop to compare all the chars in forward common area
			for (int j = i; j <= i + range && j < length2 ; j++)	{
				if (s1.charAt(i) == s2.charAt(j))	{
					m++;
					match1[i] = true;	//bool arrays to mark matches
					match2[j] = true;
				}
			}	
			//loop to compare all the chars in backward common area
			for (int j = i - 1; j >= i - range && j >= 0 ; j--)	{	
				if (s1.charAt(i) == s2.charAt(j))	{
					m++;
					match1[i] = true;
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
		
		if (m == 0)	distance = 0;
		else	distance = (double)(m/length1 + m/length2 + (m-t)/m)/3;	
	}
}
