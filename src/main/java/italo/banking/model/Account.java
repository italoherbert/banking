package italo.banking.model;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Account {

	private int id;
	
	private String holder;
	
	private double balance;
	
	private Date creationDate;
	
}
