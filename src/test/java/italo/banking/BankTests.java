package italo.banking;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import italo.banking.exception.AccountNotFoundException;
import italo.banking.exception.CreditNotEnoughException;
import italo.banking.exception.DateBeforeSchedulingDateException;
import italo.banking.exception.DestAccountNotFoundException;
import italo.banking.exception.HolderAlreadyExistsException;
import italo.banking.exception.InvalidDateFormatException;
import italo.banking.exception.SourceAccountNotFoundException;
import italo.banking.model.request.SaveAccountRequest;
import italo.banking.model.request.TransactionRequest;
import italo.banking.model.response.AccountResponse;
import italo.banking.model.response.transaction.TransactionResponse;
import italo.banking.service.AccountService;
import italo.banking.service.TransactionService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class BankTests {		
	
	@Autowired
	private AccountService accountService = new AccountService();
	
	@Autowired
	private TransactionService transactionService = new TransactionService();
		
	@Test
	void test() {
		SaveAccountRequest req = new SaveAccountRequest();
		req.setHolder( "Ítalo Herbert" );
		
		AccountResponse account1 = null;				
		try {
			account1 = accountService.registryAccount( req );
			
			accountService.registryAccount( req );			
			fail( "Deveria lançar exceção." );
		} catch (HolderAlreadyExistsException e) {
			
		}	
		
		req.setHolder( "Ítala Kelly");
		AccountResponse account2 = null;
		try {
			account2 = accountService.registryAccount( req );			
		} catch (HolderAlreadyExistsException e) {
			
		}
		
		assertNotNull( account1 );
		assertNotNull( account2 );
						
		try {
			accountService.credit( account1.getId(), 800 );			
			accountService.credit( account2.getId(), 500 );			
			try {
				accountService.debit( account2.getId(), 300 );
			} catch (CreditNotEnoughException e) {
				fail( "Não deveria tratar exceção");
			}
			
			try {
				accountService.debit( account2.getId(), 1000 );
				fail( "Deveria lançar exceção de crédito não suficiente" );
			} catch (CreditNotEnoughException e) {
				
			}									
		} catch ( AccountNotFoundException e ) {
			
		}
		
		TransactionRequest treq = new TransactionRequest();
		treq.setOperationDate( "19/05/2021" );
		treq.setValue( 0 );
		try {
			accountService.scheduleTransfer( account1.getId(), account2.getId(), treq );
			fail( "Deveria lançar exceção de data de operação inferior a data de agendamento" );
		} catch (DestAccountNotFoundException | InvalidDateFormatException
				| SourceAccountNotFoundException e) {
			fail( "Não deveria lançar essas exceções" );
		} catch ( DateBeforeSchedulingDateException e ) {
			
		}
		
		TransactionRequest treq2 = new TransactionRequest();
		treq2.setOperationDate( "19/05/2021" );
		treq2.setValue( 1000 );
		try {
			accountService.scheduleTransfer( account1.getId(), account2.getId(), treq2 );
			fail( "Deveria lançar exceção de data de operação inferior a data de agendamento" );
		} catch (DestAccountNotFoundException | InvalidDateFormatException
				| SourceAccountNotFoundException e) {
			fail( "Não deveria lançar essas exceções" );
		} catch ( DateBeforeSchedulingDateException e ) {
			
		}
		
		TransactionRequest treq3 = new TransactionRequest();
		treq3.setOperationDate( "02/06/2021" );
	 	treq3.setValue( 1000 );
		try {
			accountService.scheduleTransfer( account1.getId(), account2.getId(), treq3 );
		} catch (DestAccountNotFoundException | InvalidDateFormatException
				| SourceAccountNotFoundException | DateBeforeSchedulingDateException e) {
			fail( "Não deveria lançar essas exceções" );
		}
		
		TransactionRequest treq4 = new TransactionRequest();
		treq4.setOperationDate( "02/06/2021" );
		treq4.setValue( 100 );
		try {
			accountService.scheduleTransfer( account1.getId(), account2.getId(), treq4 );
		} catch (DestAccountNotFoundException | InvalidDateFormatException
				| SourceAccountNotFoundException | DateBeforeSchedulingDateException e) {
			fail( "Não deveria lançar essas exceções" );
		}
		
		try {
			List<TransactionResponse> list = transactionService.execSchedulingTransactions( "02/06/2021" );			
			assertEquals( list.size(), 1 );
		} catch (InvalidDateFormatException e) {
			
		}
		
		try {
			AccountResponse account3 = accountService.findById( account1.getId() );
			assertTrue( account3.getBalance() < 700 );
			
			AccountResponse account4 = accountService.findById( account2.getId() );
			assertTrue( account4.getBalance() == 300 );
		} catch (AccountNotFoundException e) {
			fail( "Não deveria lançar essa exceção" );
		}
							
	}

}
