package com.service.account.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.account.dto.AccountDto;
import com.service.account.dto.CustomerAccountDTO;
import com.service.account.response.ServiceResponse;
import com.service.account.service.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;
    
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);
    
//  Get current account service instance info
    @GetMapping("/instance-info")
    public String getInstanceInfo() {
        return "Instance running: " + applicationName + " on port " + serverPort;
    }
	
//	Add new account
	@PostMapping("addAccount")
	public ResponseEntity<ServiceResponse<AccountDto>> addAccount(@RequestBody AccountDto account){		
		return accountService.createAccount(account);
	}
	
//	Fetch single account with customer data
	@GetMapping("getAccount")
	public ResponseEntity<ServiceResponse<CustomerAccountDTO>> fetchAccountCustomerInfo(@RequestHeader(value = "id", required = true) UUID id) {
		return accountService.fetchAccountCustomerInfo(id);
	}
	
//	Make a deposit
	@PutMapping("accountDeposit")
	public ResponseEntity<ServiceResponse<AccountDto>> depositMoney(@RequestHeader(value = "id", required = true) UUID id,
			@RequestHeader(value = "amount", required = true) BigDecimal amount) {
		return accountService.updateAccountBalance(id, amount, true);
	}
	
//	Make a withdrawal
	@PutMapping("accountWithdraw")
	public ResponseEntity<ServiceResponse<AccountDto>> withdrawMoney(@RequestHeader(value = "id", required = true) UUID id, 
			@RequestHeader(value = "amount", required = true) BigDecimal amount) {
		return accountService.updateAccountBalance(id, amount, false);
	}
	
//	Delete an account only
	@DeleteMapping("deleteAccounts")
	public ResponseEntity<ServiceResponse<AccountDto>> deleteAccount(@RequestHeader(value = "id", required = true) UUID id,
			@RequestHeader(value = "accountId", required = false, defaultValue = "true") boolean accountId) {
		logger.info("Instance running: " + applicationName + " on port " + serverPort);
		return accountService.deleteAccount(id, accountId);
	}
	
//	Fetch single or all accounts only (Used by customer service)
	@GetMapping("accountsDetails")
	public ResponseEntity<ServiceResponse<AccountDto>> fetchAccounts(@RequestHeader(value = "id", required = false) UUID id,
			@RequestHeader(value = "accountId", required = false, defaultValue = "true") boolean accountId) {
		logger.info("Instance running: " + applicationName + " on port " + serverPort);
		return accountService.fetchAccounts(id, accountId);
	}
}
