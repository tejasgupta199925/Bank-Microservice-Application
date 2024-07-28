package com.service.account.config;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.service.account.dto.CustomerDto;
import com.service.account.response.ServiceResponse;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface FeignConfig {

	@GetMapping("customer/fetchCustomer/{id}")
	public ResponseEntity<ServiceResponse<CustomerDto>> fetchCustomer(@PathVariable("id") UUID id);

}
