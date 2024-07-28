package com.service.customer.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.service.customer.dto.CustomerAccountDTO;
import com.service.customer.dto.CustomerDto;
import com.service.customer.response.ServiceResponse;

public interface CustomerService {

	public ResponseEntity<ServiceResponse<CustomerDto>> createCustomer(CustomerDto customer) throws Exception;
	
	public ResponseEntity<ServiceResponse<CustomerAccountDTO>> getAllCustomers(UUID id);
	
	public ResponseEntity<ServiceResponse<CustomerDto>> updateCustomer(UUID id, CustomerDto customer);
	
	public ResponseEntity<ServiceResponse<Void>> deleteCustomer(UUID id);
	
	public ResponseEntity<ServiceResponse<CustomerDto>> fetchCustomer(UUID id);
	
}
