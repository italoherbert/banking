package italo.banking.component;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import italo.banking.exception.AccountNotFoundException;
import italo.banking.exception.CreditNotEnoughException;
import italo.banking.exception.DateBeforeSchedulingDateException;
import italo.banking.exception.DestAccountNotFoundException;
import italo.banking.exception.InvalidDateFormatException;
import italo.banking.exception.SourceAccountNotFoundException;
import italo.banking.model.Account;
import italo.banking.model.Transaction;
import italo.banking.model.request.TransactionRequest;
import italo.banking.model.response.AccountResponse;
import italo.banking.model.response.transaction.CreditResponse;
import italo.banking.model.response.transaction.DebitResponse;
import italo.banking.model.response.transaction.TransactionResponse;
import italo.banking.model.response.transaction.TransferResponse;
import italo.banking.util.DateUtil;

@Component
public class TransactionManager {

	@Autowired
	private DateUtil dateUtil;
	
	@Autowired
	private WithRateCalculator withRateCalculator;
	
	private LinkedList<Transaction> schenduleTransactions = new LinkedList<>();	
	private LinkedList<Account> accounts = new LinkedList<>();
	private int startId = 1000000;
			
	public void addAccount( Account a ) {
		accounts.add( a );
	}
	
	public void addScheduleTransaction( Transaction t ) {
		schenduleTransactions.add( t );
	}
	
	public void removeScheduleTransaction( int i ) {
		schenduleTransactions.remove( i );
	}
	
	public int nextId() {
		return startId++;
	}
	
	public void applyRate( int accountId, int days ) throws AccountNotFoundException {
		Account a = this.getById( accountId );		
		a.setBalance( withRateCalculator.calculatesValueWithRate( days, a.getBalance() ) ); 
	}
	
	public void execTransferOperation( int srcAccountId, int destAccountId, double value ) 
			throws SourceAccountNotFoundException, DestAccountNotFoundException, CreditNotEnoughException {
		
		boolean srcAccountExist = this.existById( srcAccountId );
		boolean destAccountExist = this.existById( destAccountId );
		
		if ( srcAccountExist && destAccountExist ) {
			try {
				this.execDebitOperation( srcAccountId, value );
				this.execCreditOperation( destAccountId, value );
			} catch (AccountNotFoundException e) {
				
			}		
		} else {
			if ( srcAccountExist )
				throw new DestAccountNotFoundException();
			if ( destAccountExist )
				throw new SourceAccountNotFoundException();
		}
	}
	
	public void execCreditOperation( int accountId, double value ) throws AccountNotFoundException {
		Account a = this.getById( accountId );
		
		a.setBalance( a.getBalance() + value ); 		
	}
	
	public void execDebitOperation( int accountId, double value ) throws AccountNotFoundException, CreditNotEnoughException {
		Account a = this.getById( accountId );
		
		if ( a.getBalance() < value )
			throw new CreditNotEnoughException();
		
		a.setBalance( a.getBalance() - value ); 
	}
	
	public boolean existsAccountByHolder( String holder ) {
		for( Account a : accounts )
			if ( a.getHolder().equalsIgnoreCase( holder ) )
				return true;
		return false;
	}
					
	public Account getById( int accountId ) throws AccountNotFoundException {
		for( Account a : accounts )
			if ( a.getId() == accountId )				
				return a;
					
		throw new AccountNotFoundException();
	}
	
	public boolean existById( int accountId ) {
		for( Account a : accounts )
			if ( a.getId() == accountId )				
				return true;
					
		return false;
	}
	
	public TransactionResponse createTransactionResponse( int type ) {
		switch( type ) {
			case Transaction.CREDIT: return new CreditResponse();
			case Transaction.DEBIT: return new DebitResponse();
			case Transaction.TRANSFER: return new TransferResponse();
		}
		return null;
	}
		
	public void carregaTransaction( Transaction t, TransactionRequest request ) throws InvalidDateFormatException, DateBeforeSchedulingDateException {
		try {			
			t.setOperationDate( dateUtil.toDate( request.getOperationDate() ) );
			t.setSchedulingDate( new Date() );
			t.setValue( request.getValue() );
			
			if ( t.getOperationDate().before( t.getSchedulingDate() ) )
				throw new DateBeforeSchedulingDateException();
			
		} catch ( ParseException e ) {
			throw new InvalidDateFormatException();
		}
	}
	
	public void carregaTransactionResponse( TransactionResponse resp, Transaction t ) {
		resp.setOperationDate( dateUtil.toString( t.getOperationDate() ) );
		resp.setSchedulingDate( dateUtil.toString( t.getSchedulingDate() ) ); 
		resp.setValue( t.getValue() );		
	}
	
	public void carregaAccountResponse( AccountResponse resp, Account a ) {
		resp.setId( a.getId() ); 
		resp.setHolder( a.getHolder() );
		resp.setCreationDate( dateUtil.toString( a.getCreationDate() ) );
		resp.setBalance( a.getBalance() );		
	}

	public LinkedList<Transaction> getSchenduleTransactions() {
		return schenduleTransactions;
	}

	public LinkedList<Account> getAccounts() {
		return accounts;
	}
	
}
