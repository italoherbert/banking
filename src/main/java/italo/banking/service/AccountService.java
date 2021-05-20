package italo.banking.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import italo.banking.component.TransactionManager;
import italo.banking.exception.AccountNotFoundException;
import italo.banking.exception.DateBeforeSchedulingDateException;
import italo.banking.exception.DestAccountNotFoundException;
import italo.banking.exception.HolderAlreadyExistsException;
import italo.banking.exception.InvalidDateFormatException;
import italo.banking.exception.SourceAccountNotFoundException;
import italo.banking.model.Account;
import italo.banking.model.Credit;
import italo.banking.model.Debit;
import italo.banking.model.Transfer;
import italo.banking.model.request.SaveAccountRequest;
import italo.banking.model.request.TransactionRequest;
import italo.banking.model.response.AccountResponse;

@Service
public class AccountService {
	
	@Autowired
	private TransactionManager manager;
			
	public void registryAccount( SaveAccountRequest request ) throws HolderAlreadyExistsException {					
		if ( manager.existsAccountByHolder( request.getHolder() ) )
			throw new HolderAlreadyExistsException();
				
		Account a = new Account();
		a.setId( manager.nextId() );
		a.setHolder( request.getHolder() );
		a.setBalance( 0 ); 
		a.setCreationDate( new Date() ); 
		
		manager.addAccount( a );
	}
	
	public AccountResponse findById( int accountId ) throws AccountNotFoundException {
		Account c = manager.getById( accountId );
		
		AccountResponse resp = new AccountResponse();
		resp.setHolder( c.getHolder() );
		resp.setBalance( c.getBalance() );
		return resp;
	}
			
	public void scheduleCredit( int accountId, TransactionRequest request ) 
			throws InvalidDateFormatException, DateBeforeSchedulingDateException, AccountNotFoundException {
		if ( !manager.existById( accountId ) )
			throw new AccountNotFoundException();
		
		Credit c = new Credit();
		c.setAccountId( accountId );
		
		manager.carregaTransaction( c, request );		
		manager.addScheduleTransaction( c );
	}	
	
	public void scheduleDebit( int accountId, TransactionRequest request ) 
			throws InvalidDateFormatException, DateBeforeSchedulingDateException, AccountNotFoundException {
		if ( !manager.existById( accountId ) )
			throw new AccountNotFoundException();
		
		Debit d = new Debit();
		d.setAccountId( accountId );
		
		manager.carregaTransaction( d, request );		
		manager.addScheduleTransaction( d );
	}
	
	public void scheduleTransfer( int srcAccountId, int destAccountId, TransactionRequest request ) 
			throws InvalidDateFormatException, DateBeforeSchedulingDateException, 
				SourceAccountNotFoundException, DestAccountNotFoundException {
		if ( !manager.existById( srcAccountId ) )
			throw new SourceAccountNotFoundException();
		if ( !manager.existById( destAccountId ) )
			throw new DestAccountNotFoundException();
		
		Transfer t = new Transfer();
		t.setAccountId( srcAccountId );
		t.setDestAccountId( destAccountId );
		
		manager.carregaTransaction( t, request );						
		manager.addScheduleTransaction( t );
	}
	
}
