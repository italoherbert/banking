package italo.banking.model.response.transaction;

import italo.banking.model.TransactionType;
import italo.banking.model.response.AccountResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public abstract class TransactionResponse {

	private AccountResponse account;

	private String schedulingDate;
	
	private String operationDate;	
	
	private double value;	
	
	public abstract TransactionType getType();
	
}
