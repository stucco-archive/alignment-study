import java.util.Date;
import java.text.*;

public class DateTimeFormat {

	private Date date;
	
	long formatNVDDateTime (String time)	{
			
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS-SS:SS");
  			date = df.parse(time);
		
		} catch	(ParseException e)	{
			e.printStackTrace();
		}
  		return date.getTime();	
	}

	long formatBugtraqDateTime(String time)	{ 

		try {
			SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy hh:mma");
  			date = df.parse(time);
  			return date.getTime();	
		
		} catch	(ParseException e)	{
			e.printStackTrace();
		}
  		return date.getTime();	
	}
}
