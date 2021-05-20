package italo.banking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Transfer extends Transaction {
	
	private int destAccountId;
	
	@Override
	public int getType() {
		return TRANSFER;
	}
		
}
