package italo.banking.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {

	private final int MILISECONDS_IN_DAY = 24 * 60 * 60 * 1000;
	
	private final SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
	
	public boolean sameDay( Date date1, Date date2 ) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime( date1 );
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime( date2 );
		
		if ( c1.get( Calendar.DAY_OF_YEAR ) == c2.get( Calendar.DAY_OF_YEAR ) && 
				c1.get( Calendar.YEAR ) == c2.get( Calendar.YEAR ) ) {
			return true;
		}
		
		return false;			
	}
	
	public int diferenceToDays( Date operationDate, Date schedulingDate ) {
		long operationDateMS = operationDate.getTime();
		long schedulingDateMS = schedulingDate.getTime();
		
		return ( (int)( operationDateMS - schedulingDateMS ) ) / MILISECONDS_IN_DAY;
	}
	
	public String toString( Date date ) {
		return sdf.format( date );
	}
	
	public Date toDate( String date ) throws ParseException {
		return sdf.parse( date );
	}
	
}
