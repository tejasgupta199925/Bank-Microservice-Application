package com.service.customer.config;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.service.customer.dto.AccountDto;
import com.service.customer.response.ServiceResponse;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface FeignConfig {
	
	@GetMapping("account/accountsDetails")
	public ResponseEntity<ServiceResponse<AccountDto>> fetchAccounts(@RequestHeader(value = "id", required = false) UUID id,
			@RequestHeader(value = "accountId", required = false, defaultValue = "true") boolean accountId);
	
	@DeleteMapping("account/deleteAccounts")
	public ResponseEntity<ServiceResponse<AccountDto>> deleteAccount(@RequestHeader(value = "id", required = true) UUID id,
			@RequestHeader(value = "accountId", required = false, defaultValue = "true") boolean accountId);
	
}
