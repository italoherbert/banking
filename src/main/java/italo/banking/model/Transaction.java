package italo.banking.model;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public abstract class Transaction {

	public final static int DEBIT = 1;
	public final static int CREDIT = 2;
	public final static int TRANSFER = 3;	

	private int accountId;
	
	private double value;

	private Date schedulingDate;
	
	private Date operationDate;
		
	public abstract int getType();
	
}
