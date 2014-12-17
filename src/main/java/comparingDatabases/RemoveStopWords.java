package alignmentStudy;
//in constructor function takes a file with stop words

import java.io.*;
import java.util.*;

public class RemoveStopWords {

	private Set<String> hm;

	public RemoveStopWords(String STOP_WORDS_FILE)	{
		
		try {
			hm = new HashSet<String>();
			InputStream i = RemoveStopWords.class.getClassLoader().getResourceAsStream(STOP_WORDS_FILE);
			BufferedReader r = new BufferedReader(new InputStreamReader(i));
			
			String str;
			while((str = r.readLine()) != null)	{
				hm.add(str);
			}
			i.close();
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
	}
	
	boolean containsString (String str)	{	
		return hm.contains(str);
	}
}
