package italo.banking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Credit extends Transaction {
				
	@Override
	public int getType() {
		return CREDIT;
	}
		
}
