package alignmentStudy;

import java.net.*;
			
public class URLClass {
				
	public URLClass()	{						
		try {
			URL url = new URL("http://support.sgi.com");
			System.out.println(url);
			System.out.println(url.getProtocol());		
		} catch (MalformedURLException e)	{
			e.printStackTrace();
		}
	}	

	public static void main(String[] args)	{
		new URLClass();
	}
}
