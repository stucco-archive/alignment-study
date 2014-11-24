package alignmentStudy;

//functiosn to compare trancated strings
//taking two strigs to compare and penalty for opening the gap, and extending it
//tends to value ends of the strings more than the middle

public class AffineGap {
	
	private String s1;
	private String s2;
	private String s1Aligned;
	private String s2Aligned;
	private double[][] P;
	private double[][] Q;
	private double[][] D;
	private int length1;
	private int length2;
	private double v;
	private double u;
	private double distance;
	
	public double affineGapDistance (String str1, String str2, double openPenalty, double extendPenalty)	{
		
		if (str1.equals(str2))  return 1.0;       //checking if strings are the same
		
		length1 = str1.length();
		length2 = str2.length();
 		
		s1 = new String (str1);
             	s2 = new String (str2);
		s1Aligned = new String();
             	s2Aligned = new String();
		P = new double [length1 + 1][length2 + 1]; 
		D = new double [length1 + 1][length2 + 1];	
		Q = new double [length1 + 1][length2 + 1];	
		v = openPenalty;
		u = extendPenalty;
		
		matricesInitialization();
		allignmentCalculation();
		distance = D[length1][length2];		//distance is the very last element of D array
		
		return normalized(distance);
	}

	void matricesInitialization()	{

		for (int i = 1; i <= length1; i++)	{
			Q[i][0] = Double.POSITIVE_INFINITY;
			D[i][0] = gapPenalty(i);
		}

		for (int j = 1; j <= length2; j++)	{
			P[0][j] = Double.POSITIVE_INFINITY;
			D[0][j] = gapPenalty(j);
		}
		D[0][0] = 0.0;
	}
	
	void allignmentCalculation ()	{

		for (int i = 1; i <= length1; i++)	{
			for (int j = 1; j <= length2; j++)	{
				P[i][j] = Math.min(D[i-1][j] + gapPenalty(1), P[i-1][j] + u);
				Q[i][j] = Math.min(D[i][j-1] + gapPenalty(1), Q[i][j-1] + u);
				D[i][j] = Math.min(Math.min(D[i-1][j-1] + costFunction(i, j), Q[i][j]), P[i][j]);
			}
		}
		
	}

	//caltulating gap penalty according to the arguments
	double gapPenalty(int gapLength)	{
		
		return v + gapLength * u;
	}	

	//returns 0.0 cost if chars are the same, 1.0 otherwise 
	double costFunction(int i, int j)	{
		
		if (s1.charAt(i-1) == s2.charAt(j-1))	return 0.0;
		else return 1.0;
				
	}
	
	//creating alignes strings
	void backtraceCalculation()	{
		
		int i = length1;
		int j = length2;

		while  (i > 0 || j > 0)	{
			if (i > 0 && j > 0 && D[i][j] == D[i-1][j-1] + costFunction(i, j))	{
				s1Aligned = s1.charAt(i-1) + s1Aligned;
				s2Aligned = s2.charAt(j-1) + s2Aligned;
				i--;
				j--;
				continue;
			}
			if (i > 0 && D[i][j] == P[i][j]) 	{
				s1Aligned = s1.charAt(i-1) + s1Aligned;
				s2Aligned = "-" + s2Aligned;
				i--;
			}
			if (j > 0 &&  D[i][j] == Q[i][j])	{
				s1Aligned = "-" + s1Aligned;
				s2Aligned = s2.charAt(j-1) + s2Aligned;
				j--;
			}	
			if (j > 0 && i == 0)	{
				s1Aligned = "-" + s1Aligned;
				s2Aligned = s2.charAt(j-1) + s2Aligned;
				j--;
			}
			if (j == 0 && i > 0)	{
				s1Aligned = s1.charAt(i-1) + s1Aligned;
				s2Aligned = "-" + s2Aligned;
				i--;
			}
		}	
	}

	void printMatrices()	{
		for (int i = 0; i <= length1; i++)	{
			for (int j = 0; j <= length2; j++)	
				System.out.print(D[i][j] + " ");
			System.out.println();
		} 	
			System.out.println();
		for (int i = 0; i <= length1; i++)	{
			for (int j = 0; j <= length2; j++)	
				System.out.print(Q[i][j] + " ");
			System.out.println();
		} 	
			System.out.println();
		for (int i = 0; i <= length1; i++)	{
			for (int j = 0; j <= length2; j++)	
				System.out.print(P[i][j] + " ");
			System.out.println();
		} 	
		
	}

	public void printAlignedStrings()	{
		
		System.out.println(s1Aligned);
		System.out.println(s2Aligned);
	}

	public String getAlignedStrOne()      {

              return s1Aligned;
      	}
 
      	public String getAlignedStrTwo()      {

              return s2Aligned;
      	}

	double normalized(double distance)	{
		
		double max = (double)Math.max(length1, length2);
		return (max - distance)/max;
	}
}
