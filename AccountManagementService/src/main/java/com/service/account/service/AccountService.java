package com.service.account.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.service.account.dto.AccountDto;
import com.service.account.dto.CustomerAccountDTO;
import com.service.account.response.ServiceResponse;

public interface AccountService {

	public ResponseEntity<ServiceResponse<AccountDto>> createAccount(AccountDto account);
	
	public ResponseEntity<ServiceResponse<AccountDto>> fetchAccounts(UUID id, boolean accountId);
	
	public ResponseEntity<ServiceResponse<AccountDto>> updateAccountBalance(UUID id, BigDecimal amount, boolean deposit);
	
	public ResponseEntity<ServiceResponse<AccountDto>> deleteAccount(UUID id, boolean accountId);
	
	public ResponseEntity<ServiceResponse<CustomerAccountDTO>> fetchAccountCustomerInfo(UUID id);
}
