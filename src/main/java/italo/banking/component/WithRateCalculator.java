package italo.banking.component;

import org.springframework.stereotype.Component;

@Component
public class WithRateCalculator { 
	
	public double calculatesValueWithRate( int days, double value ) {
		if ( days == 0 )
			return value - 3 + ( 0.03d * value );
		
		if ( days <= 10 )
			return value - ( 12 * days );
		
		if ( days <= 20 )
			return value - ( 0.08d * value );
		
		if ( days <= 30 )
			return value - ( 0.06d * value );
		
		if ( days <= 40 )
			return value - ( 0.04d * value );
		
		if ( value > 100000 )
			return value - ( 0.02d * value );
		
		return value;
	}
	
}
