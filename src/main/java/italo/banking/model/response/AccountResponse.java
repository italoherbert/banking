package italo.banking.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AccountResponse {

	private String holder;
	
	private double balance;
	
	private String creationDate;
	
}
