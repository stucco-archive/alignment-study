/*
function returns coefficients of the polynomial
p(s) = coeff[o] + coef[1]x + coef[2]s^2 + ... + coeff[n]x^n
*/

package alignmentStudy;

import java.util.*;

public class PolyFit	{

	private double[] x;
	private double[] y;
	private double[] coeff;

	private double tol;

	public PolyFit() {
	
		x = new double[4];
		y = new double[4];
					
		tol = 1.0e-12;
		
		//example data
		x[0] = 1;
		x[1] = 2;
		x[2] = 3;
		x[3] = -1;

		y[0] = 8;
		y[1] = 26;
		y[2] = 72;
		y[3] = 2;

		polyFit(1);		
		
		for (int i = 0; i < coeff.length; i++)
			System.out.print(coeff[i] + " ");
		System.out.println();
	}	

	void polyFit(int size)	{

		double[][] a = new double[size+1][size+1];
		double[] b = new double[size+1];
		double[] s = new double[size*2+1];
		double temp;

		for (int i = 0; i < x.length; i++)	{
			temp = y[i];
			for (int j = 0; j < size + 1; j++)	{
				b[j] = b[j] + temp;
				temp = temp*x[i];
				
			}
			temp = 1.0;
			for(int j = 0; j < size*2+1; j++)	{
				s[j] = s[j] + temp;
				temp = temp*x[i];
			}
		}
		for (int i = 0; i < size+1; i++)	{
			for (int j = 0; j < size + 1; j++)	
				a[i][j] = s[i+j];
		}
		
		gaussPivot(a,b);
		coeff = b;
	}
	
	void gaussPivot(double[][] a, double[] b)	{	
		
		double[] s = new double[b.length];
		double lam;
		int n = b.length;
		int p;

		for (int i = 0; i < n; i++)	{
			double max = 0.0;
			for(int j = 0; j < a[i].length; j++)	{
				if (max < Math.abs(a[i][j]))	max = Math.abs(a[i][j]);
			}
			s[i] = max;
		}
							
		//Row interchange
		for (int k = 0; k < n-1; k++)	{
			double[]temp = new double[n-k];
			int f = 0;
			for (int i = k ; i < n; i++)	{
				temp[f] = Math.abs(a[i][k]/s[i]);
				f++;
			}
			p = argmax(temp) + k;
			if (Math.abs(a[p][k]) < tol) System.out.println("Matrix is singular");
			if (p != k)	{
				double t;

				t = b[k];
				b[k] = b[p];
				b[p] = t;
				
				t = s[k];
				s[k] = s[p];
				s[p] = t;
				
				for (int i = 0; i < a[k].length; i++)	{
					t = a[k][i];
					a[k][i] = a[p][i];
					a[p][i] = t;
				}
			}
			
			for (int i = k+1; i < n; i++)	{
				if (a[i][k] != 0.0)	{
					lam = a[i][k]/a[k][k];
					for (int j = k+1; j < n; j++)	
						a[i][j] = a[i][j] - lam*a[k][j];
					b[i] = b[i] - lam*b[k];
				}
			}
		}
		if (Math.abs(a[n-1][n-1]) < tol)	System.out.println("Matrix is singular");

		//Backsubstitution
		b[n-1] = b[n-1]/a[n-1][n-1];
		for (int k = n-2; k > -1; k--)	
			b[k] = (b[k] - dotProduct(a, b, k, n))/a[k][k];

	}

	double dotProduct(double[][] a, double[] b, int k, int n)	{
					
		double dotPr = 0;
		
		for (int i = k+1; i < n; i++)	
			dotPr = dotPr + a[k][i] * b[i];	
		
		return dotPr;
	} 

	int argmax(double[] array) {
    		
		int n = array.length;
    		int i;
    		double max;
    		int argmax;

    		if (n == 0) {
      			argmax = -1;
    		} 
		else {
      			max = array[0];
      			argmax = 0;
      			for (i=1;i<n;i++) {
				if (max < array[i]) {
	  				max = array[i];
	  				argmax = i;
				}
      			}
    		}

    		return argmax;
  	}

	public static void main(String[] args)	{
	
		new PolyFit();
	}
}
