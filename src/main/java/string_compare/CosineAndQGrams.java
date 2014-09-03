package alignmentStudy;

import java.util.*;

public class CosineAndQGrams extends Comparison {

	private Map<Integer, String> textOne;
        private Map<Integer, String> textTwo;
	private Map<Integer, Map<String, Integer>>  mapOne; 	//size of JSON array
	private Map<Integer, Map<String, Integer>>  mapTwo; 	//size of JSON array
	private Map<String, Integer> allWords;
	private PorterStemmer ps;

	public CosineAndQGrams()	{
		
		mapOne = new HashMap<Integer, Map<String, Integer>>(); 	
		mapTwo = new HashMap<Integer, Map<String, Integer>>(); 	
		allWords = new HashMap<String, Integer>();				
		ps = new PorterStemmer();
	}

	public void setTextMaps (Map<Integer, String> textOne, Map<Integer, String> textTwo)	{
			
		this.textOne = textOne;                          
 	        this.textTwo = textTwo; 

		storeArrayOneIntoMaps();	//arrayOne into mapOne and allWords
		storeArrayTwoIntoMaps();	//arrayTwo into mapTwo and allWords
	}
	
	//creating map for cosine similarity and tf-idf
	void storeArrayOneIntoMaps()	{
						
		for (int key : textOne.keySet())	{
			Map <String, Integer> map = new HashMap<String, Integer>();
                        stemObjectAndAddToAllWordsMap (textOne.get(key), map);
			mapOne.put(key, map);					
		}
	}

	//creating map for cosine similarity and tf-idf
	void storeArrayTwoIntoMaps()	{
		for (int key : textTwo.keySet())	{
			Map <String, Integer> map = new HashMap<String, Integer>();
                        stemObjectAndAddToAllWordsMap (textTwo.get(key), map);
			mapTwo.put(key, map);									
		}
	}

	//removing suffixes from all words 
	void  stemObjectAndAddToAllWordsMap (String s, Map<String, Integer> map)	{
		
		String[] str = s.split(" ");
		String substring, key;
		boolean done = false;
		int count;
	
		for (int j = 0; j < str.length; j++)  {
			if (super.isAStopWord(str[j])) continue;	//removing stop words
			str[j] = str[j].toLowerCase();    
			str[j] = super.removeChars(str[j]);	//removes extra chars and trims extra space;
			ps.add(str[j].toCharArray(), str[j].length());
			ps.stem();
			key = "#" + ps.toString() + "#";
			
                        for (int i = 0; i <= key.length() - 2; i++)  {
                                if (map.get(substring = key.substring(i, i + 2)) == null)	map.put(substring, 1);
				else	{
					count =  map.get(substring);
					map.put(substring, ++count); 
				}
				
				if (allWords.get(substring) == null)	allWords.put(substring, 1);
				else if (!done){
					count = allWords.get(substring);
					allWords.put(substring, ++count);
					done = true;
				}
                        }
                }
	}
	//compares descriptions using WHIRL or cosine similarity
	double getSimilarityScore (int i, int j)	{
	
		double 	vDinominator = 0.0, uDinominator = 0.0, numerator = 0.0; 
		double[] v = new double [allWords.size()];
		double[] u = new double [allWords.size()];

		calculateUOrV (mapOne.get(i), v);
		calculateUOrV (mapTwo.get(j), u);
		
		for (int k = 0; k < allWords.size(); k++)	{
			numerator = numerator + (v[k] * u[k]);
			vDinominator = vDinominator + (v[k] * v[k]); 
			uDinominator = uDinominator + (u[k] * u[k]);					
		}

		if (vDinominator == 0.0 | uDinominator == 0.0)	return 0.0;
		else	return numerator / (Math.sqrt(vDinominator) * Math.sqrt(uDinominator));
	}
	
	//helper function for compareDescriptions
	void calculateUOrV (Map <String, Integer> map, double[] array)	{
		
		int x = 0;
		for (String s : allWords.keySet())	{
			if (map.get(s) == null)	array[x] = 0.0;
			else array[x] = (double)map.get(s);
			x++;
		}					
	}
}
