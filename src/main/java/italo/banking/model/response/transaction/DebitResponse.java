package italo.banking.model.response.transaction;

import italo.banking.model.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DebitResponse extends TransactionResponse {
	
	@Override
	public TransactionType getType() {
		return TransactionType.DEBIT;
	}		
	
}
