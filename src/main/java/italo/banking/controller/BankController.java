package italo.banking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import italo.banking.exception.AccountNotFoundException;
import italo.banking.exception.DateBeforeSchedulingDateException;
import italo.banking.exception.DestAccountNotFoundException;
import italo.banking.exception.HolderAlreadyExistsException;
import italo.banking.exception.InvalidDateFormatException;
import italo.banking.exception.SourceAccountNotFoundException;
import italo.banking.model.request.SaveAccountRequest;
import italo.banking.model.request.TransactionRequest;
import italo.banking.model.response.AccountResponse;
import italo.banking.model.response.ErroResponse;
import italo.banking.model.response.transaction.TransactionResponse;
import italo.banking.service.AccountService;
import italo.banking.service.TransactionService;

@RestController
@RequestMapping(value="/api/bank")
public class BankController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionService scheduleTransactionService;
		
	@PostMapping("/create/account")
	public ResponseEntity<Object> createAccount( @RequestBody SaveAccountRequest request ) {
		try {
			accountService.registryAccount( request );
			return ResponseEntity.ok().build();
		} catch (HolderAlreadyExistsException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.HOLDER_ALREADY_EXISTS ) );
		}
	}
	
	@PostMapping(value="/cash/{accountId}")
	public ResponseEntity<Object> cash( @PathVariable int accountId, @RequestBody TransactionRequest request ) {
		try {
			accountService.scheduleDebit( accountId, request );
			return ResponseEntity.ok().build();
		} catch (InvalidDateFormatException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.INVALID_DATE_FORMAT ) );
		} catch (DateBeforeSchedulingDateException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.DATE_BEFORE_SCHEDULING_DATE ) );
		} catch (AccountNotFoundException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.ACCOUNT_NOT_FOUND ) );
		}
	}
	
	@PostMapping(value="/deposit/{accountId}")
	public ResponseEntity<Object> deposit( @PathVariable int accountId, @RequestBody TransactionRequest request ) {
		try {
			accountService.scheduleCredit( accountId, request );
			return ResponseEntity.ok().build();
		} catch (InvalidDateFormatException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.INVALID_DATE_FORMAT ) );
		} catch (DateBeforeSchedulingDateException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.DATE_BEFORE_SCHEDULING_DATE ) );
		} catch (AccountNotFoundException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.ACCOUNT_NOT_FOUND ) );
		}
	}
	
	@PostMapping(value="/transfer/{accountId}/{destAccountId}")
	public ResponseEntity<Object> transfer(
			@PathVariable int accountId, @PathVariable int destAccountId, @RequestBody TransactionRequest request ) {
		try {
			accountService.scheduleTransfer( accountId, destAccountId, request );
			return ResponseEntity.ok().build();
		} catch (InvalidDateFormatException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.INVALID_DATE_FORMAT ) );
		} catch (DateBeforeSchedulingDateException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.DATE_BEFORE_SCHEDULING_DATE ) );
		} catch (SourceAccountNotFoundException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.SOURCE_ACCOUNT_NOT_FOUND ) );
		} catch (DestAccountNotFoundException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.DEST_ACCOUNT_NOT_FOUND ) );
		}
	}
	
	@GetMapping(value="/get/account/{accountId}")
	public ResponseEntity<Object> findAccount( @PathVariable int accountId ) {
		try {
			AccountResponse resp = accountService.findById( accountId );
			return ResponseEntity.ok( resp );
		} catch (AccountNotFoundException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.ACCOUNT_NOT_FOUND ) );
		}
	}
	
	@GetMapping(value="/lista/{accountId}")
	public ResponseEntity<Object> listaTransactionsByAccountId( @PathVariable int accountId ) {
		try {
			List<TransactionResponse> list = scheduleTransactionService.listTransactionsByAccountId( accountId );
			return ResponseEntity.ok( list );
		} catch (SourceAccountNotFoundException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.SOURCE_ACCOUNT_NOT_FOUND ) );
		} catch (DestAccountNotFoundException e) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.DEST_ACCOUNT_NOT_FOUND ) );			
		}
	}
	
	@PostMapping(value="/exec/scheduling/transactions/today")
	public ResponseEntity<Object> execSchedulingTransactionsToday() {
		List<TransactionResponse> transactionsWithoutEnough = scheduleTransactionService.execTodaySchedulingTransactions();
		return ResponseEntity.ok( transactionsWithoutEnough );
	}
	
	@PostMapping(value="/exec/scheduling/transactions/{datestr}")
	public ResponseEntity<Object> execSchedulingTransactions( @PathVariable String datestr ) {		
		try {
			List<TransactionResponse> transactionsWithoutEnough = scheduleTransactionService.execSchedulingTransactions( datestr );
			return ResponseEntity.ok( transactionsWithoutEnough );
		} catch ( InvalidDateFormatException e ) {
			return ResponseEntity.badRequest().body( new ErroResponse( ErroResponse.INVALID_DATE_FORMAT ) );			
		}
	}
	
}
