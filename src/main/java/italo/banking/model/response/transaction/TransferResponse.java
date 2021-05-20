package italo.banking.model.response.transaction;

import italo.banking.model.TransactionType;
import italo.banking.model.response.AccountResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TransferResponse extends TransactionResponse {
	
	private AccountResponse destAccount;
		
	@Override
	public TransactionType getType() {
		return TransactionType.TRANSFER;
	}	
	
}
