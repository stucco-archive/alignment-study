package alignmentStudy;

//function takes two args: xml file that should be converted and 
//output file name

import java.io.*;

import net.sf.json-lib.JSON;
import net.sf.json-lib.xml.XMLSerializer;

import org.apache.commons.io.IOUtils;

public class XMLtoJSON {

	public XMLtoJSON (String[] args)	{
		try {
                	InputStream in = new FileInputStream(args[0]);
                	FileWriter fw = new FileWriter(args[1]);	
			BufferedWriter bw = new BufferedWriter(fw);

                	String xml = IOUtils.toString(in);                
                	XMLSerializer xmlSerializer = new XMLSerializer(); 
			
			xml = xml.replaceAll("[\t\n]","");	//removing hidden chars
			
			JSON json = xmlSerializer.read(xml);  
			bw.write(json.toString(2));
			
			in.close();
			bw.close();
		} catch (FileNotFoundException e)	{
			e.printStackTrace();	
		} catch (IOException e)	{
			e.printStackTrace();
		}
	}

        public static void main(String[] args) {
        	new XMLtoJSON(args);
	}
}
