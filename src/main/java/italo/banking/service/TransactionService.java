package italo.banking.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import italo.banking.component.TransactionManager;
import italo.banking.exception.AccountNotFoundException;
import italo.banking.exception.CreditNotEnoughException;
import italo.banking.exception.DestAccountNotFoundException;
import italo.banking.exception.InvalidDateFormatException;
import italo.banking.exception.SourceAccountNotFoundException;
import italo.banking.model.Account;
import italo.banking.model.Transaction;
import italo.banking.model.Transfer;
import italo.banking.model.response.AccountResponse;
import italo.banking.model.response.transaction.TransactionResponse;
import italo.banking.model.response.transaction.TransferResponse;
import italo.banking.util.DateUtil;

@Service
public class TransactionService {
	
	@Autowired
	private TransactionManager manager;
		
	@Autowired
	private DateUtil dateUtil;		
	
	public List<TransactionResponse> listTransactionsByAccountId( int accountId ) throws SourceAccountNotFoundException, DestAccountNotFoundException {				
		AccountResponse accountResp = this.createAndBuildAccountResponse( accountId ); 
		
		List<TransactionResponse> lista = new ArrayList<>();
		for( Transaction t : manager.getSchenduleTransactions() ) {
			if ( t.getAccountId() == accountId ) {
				TransactionResponse resp = manager.createTransactionResponse( t.getType() );
				manager.carregaTransactionResponse( resp, t );
								
				resp.setAccount( accountResp );
				
				if ( t.getType() == Transaction.TRANSFER ) {
					try {
						int destAccountId = ((Transfer)t).getDestAccountId();
						AccountResponse destAccountResp = this.createAndBuildAccountResponse( destAccountId );
						
						((TransferResponse)resp).setDestAccount( destAccountResp ); 
					} catch (AccountNotFoundException e) {
						throw new DestAccountNotFoundException();
					}					
				}
				
				lista.add( resp );
			}
		}
		return lista;		
	}
	public List<TransactionResponse> execTodaySchedulingTransactions() {
		return this.execSchedulingTransactions( new Date() );
	}

	public List<TransactionResponse> execSchedulingTransactions( String strdate ) throws InvalidDateFormatException {
		try {
			Date date = dateUtil.toDate( strdate );
			return this.execSchedulingTransactions( date );
		} catch (ParseException e) {
			throw new InvalidDateFormatException();
		}
	}
			
	public List<TransactionResponse> execSchedulingTransactions( Date date ) {			
		List<TransactionResponse> transactionsWithoutEnoughCredits = new ArrayList<>();
		LinkedList<Transaction> transactions = manager.getSchenduleTransactions();
		while( !transactions.isEmpty() ) {
			Transaction t = transactions.pop();
			
			if ( !dateUtil.sameDay( t.getOperationDate(), date ) )
				continue;
			
			switch( t.getType() ) {
				case Transaction.CREDIT:
					this.execCreditOperation( t );
					break;
				case Transaction.DEBIT:
					this.execDebitOperation( t, transactionsWithoutEnoughCredits );
					break;
				case Transaction.TRANSFER:
					this.execTransferOperation( t, transactionsWithoutEnoughCredits );					
					break;
				default:
					throw new RuntimeException( "TIPO DE TRANSAÇÃO INVÁLIDA" );
			}						
		}				
		
		return transactionsWithoutEnoughCredits;
	}
	
	private AccountResponse createAndBuildAccountResponse( int accountId ) throws DestAccountNotFoundException {
		try {
			Account account = manager.getById( accountId );
			
			AccountResponse accountResp = new AccountResponse();			
			manager.carregaAccountResponse( accountResp, account );
			
			return accountResp; 
		} catch (AccountNotFoundException e) {
			throw new DestAccountNotFoundException();
		}
	}
			
	private void execCreditOperation( Transaction t ) {
		try {
			manager.execCreditOperation( t.getAccountId(), t.getValue() );						
		} catch (AccountNotFoundException e) {
			throw new RuntimeException( "INCONSISTÊNCIA: CONTA NÃO ENCONTRADA PELO ID="+t.getAccountId()+"." );
		} 
	}
	
	private void execDebitOperation( Transaction t, List<TransactionResponse> transactionWithoutEnoughCredits ) {
		try {
			manager.execDebitOperation( t.getAccountId(), t.getValue() );						
		} catch (AccountNotFoundException e) {
			throw new RuntimeException( "INCONSISTÊNCIA: CONTA NÃO ENCONTRADA PELO ID="+t.getAccountId()+"." );
		} catch (CreditNotEnoughException e) {
			TransactionResponse resp = manager.createTransactionResponse( t.getType() );
			manager.carregaTransactionResponse( resp, t ); 
			transactionWithoutEnoughCredits.add( resp );			
		}
	}
	
	private void execTransferOperation( Transaction t, List<TransactionResponse> transactionWithoutEnoughCredits ) {
		int srcAccountId = ((Transfer)t).getAccountId();
		int destAccountId = ((Transfer)t).getDestAccountId();
		try {									
			double value = t.getValue();												
			manager.execTransferOperation( srcAccountId, destAccountId, value );
			
			try {
				int days = dateUtil.diferenceToDays( t.getOperationDate(), t.getSchedulingDate() );			
				manager.applyRate( srcAccountId, days );
			} catch ( AccountNotFoundException e ) {
				throw new SourceAccountNotFoundException();
			}			
		} catch ( DestAccountNotFoundException e ) {
			throw new RuntimeException( "INCONSISTÊNCIA: CONTA NÃO ENCONTRADA PELO ID="+srcAccountId+"." );
		} catch ( SourceAccountNotFoundException e ) {
			throw new RuntimeException( "INCONSISTÊNCIA: CONTA NÃO ENCONTRADA PELO ID="+destAccountId+"." );
		} catch ( CreditNotEnoughException e) {
			TransactionResponse resp = manager.createTransactionResponse( t.getType() );
			manager.carregaTransactionResponse( resp, t ); 
			transactionWithoutEnoughCredits.add( resp );
		} 
	}
		
}
