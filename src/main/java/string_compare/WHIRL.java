package alignmentStudy;

import java.util.*;
import java.io.*;

public class WHIRL extends Comparison {

	private Map<String, String> textOne;
        private Map<String, String> textTwo;
	private Map<String, Map<String, Integer>>  mapOne; 	//size of JSON array
	private Map<String, Map<String, Integer>>  mapTwo; 	//size of JSON array
	private Map<String, Integer> allWords;
	private PorterStemmer ps;
	private String WHIRL_CONFIG_FILE = "WHIRLConfigFile.txt";	

	public WHIRL()	{
		
		mapOne = new HashMap<String, Map<String, Integer>>(); 	
		mapTwo = new HashMap<String, Map<String, Integer>>(); 	
		allWords = new HashMap<String, Integer>();				
		ps = new PorterStemmer();
	}

	void loadConfig()	{
	
		try	{
			InputStream i = WHIRL.class.getClassLoader().getResourceAsStream(WHIRL_CONFIG_FILE);
			BufferedReader br = new BufferedReader (new InputStreamReader (i));	
			String str = new String();
			
			while ((str = br.readLine()) != null)	{
				String[] line = str.split(" ");
				allWords.put(line[0], Integer.parseInt(line[1]));
			}
			i.close();
			br.close();
		} catch (FileNotFoundException e)	{
			e.printStackTrace();
		} catch (IOException e)	{
			e.printStackTrace();
		}
	}

	//updating config file
	void close()	{
		
		try	{
			BufferedWriter bw = new BufferedWriter (new FileWriter(WHIRL_CONFIG_FILE));	
					
			for (String key: allWords.keySet())	{
				bw.write(key + " " + allWords.get(key)); 
			}

			bw.close();
		} catch (FileNotFoundException e)	{
			e.printStackTrace();
		} catch (IOException e)	{
			e.printStackTrace();
		}	
	}

	public void setTextMaps (Map<String, String> textOne, Map<String, String> textTwo)	{
			
		this.textOne = textOne;                          
 	        this.textTwo = textTwo; 

		storeArrayOneIntoMaps();	//arrayOne into mapOne and allWords
		storeArrayTwoIntoMaps();	//arrayTwo into mapTwo and allWords
	}
	
	//creating map for cosine similarity and tf-idf
	void storeArrayOneIntoMaps()	{
						
		for (String key : textOne.keySet())	{
			Map <String, Integer> map = new HashMap<String, Integer>();
                        stemObjectAndAddToAllWordsMap (textOne.get(key), map);
			mapOne.put(key, map);					
		}
	}

	//creating map for cosine similarity and tf-idf
	void storeArrayTwoIntoMaps()	{
		for (String key : textTwo.keySet())	{
			Map <String, Integer> map = new HashMap<String, Integer>();
                        stemObjectAndAddToAllWordsMap (textTwo.get(key), map);
			mapTwo.put(key, map);									
		}
	}
							
	//removing suffixes from all words 
	void  stemObjectAndAddToAllWordsMap (String s, Map<String, Integer> map)	{

		String[] str = s.split(" ");
		boolean done = false;
		int count;

		for (int j = 0; j < str.length; j++)  {
			if (super.isAStopWord(str[j])) continue;	//removing stop words
			str[j] = str[j].toLowerCase();    
			str[j] = removeChars(str[j]);	//removes extra chars and trims extra space;
			ps.add(str[j].toCharArray(), str[j].length());		
			ps.stem();
			str[j] = ps.toString();
                        
			if (map.get(str[j]) == null)	map.put(str[j], 1);
			else	{
				count =  map.get(str[j]);
				map.put(str[j], ++count); 
			}
										
			if (allWords.get(str[j]) == null)	allWords.put(str[j], 1);
			else if (!done){
				count = allWords.get(str[j]);
				allWords.put(str[j], ++count);
				done = true;
			}
                }
	}


	//compares descriptions using WHIRL or cosine similarity
	public double getSimilarityScore (String i, String j)	{

		if (mapOne.get(i) == null | mapTwo.get(j) == null)	return 0.0;

		double vDinominator = 0.0, uDinominator = 0.0, numerator = 0.0; 
		double[] v = new double [allWords.size()];
		double[] u = new double [allWords.size()];

		calculateV (mapOne.get(i), v);
		calculateU (mapTwo.get(j), u);
		
		for (int k = 0; k < allWords.size(); k++)	{
			numerator = numerator + (v[k] * u[k]);
			vDinominator = vDinominator + (v[k] * v[k]); 
			uDinominator = uDinominator + (u[k] * u[k]);					
		}
										
		if (vDinominator == 0.0 | uDinominator == 0.0)	return 0.0;
		else	return numerator / (Math.sqrt(vDinominator) * Math.sqrt(uDinominator));
	}

	//helper function for compareDescriptions
	void calculateV (Map <String, Integer> map, double[] array)	{

		double tf, idf;
		int x = 0;

		for (String s : allWords.keySet())	{
			if (map.get(s) == null)	array[x] = 0.0;
			else	{
				tf = (double)map.get(s);
				idf = (double)mapOne.size()/(double)allWords.get(s);	 
				array[x] = Math.log(tf + 1.0) * Math.log(idf);	
			}
			x++;					
		}
	}

	//helper function for compareDescriptions
	void calculateU (Map <String, Integer> map, double[] array)	{

		double tf, idf;
		int x = 0;

		for (String s : allWords.keySet())	{
			if (map.get(s) == null)	array[x] = 0.0;
			else	{
				tf = (double)map.get(s);
				idf = (double)mapTwo.size()/(double)allWords.get(s);	 
				array[x] = Math.log(tf + 1.0) * Math.log(idf);	
			}
			x++;
		}					
	}
}
