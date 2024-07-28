package com.service.customer.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.customer.dto.CustomerAccountDTO;
import com.service.customer.dto.CustomerDto;
import com.service.customer.response.ServiceResponse;
import com.service.customer.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;
	
	@Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;
    
    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    
//  Get current customer service instance info  
    @GetMapping("/instance-info")
    public String getInstanceInfo() {
        return "Instance running: " + applicationName + " on port " + serverPort;
    }

	
//	Add new Customer
	@PostMapping("customer")
	public ResponseEntity<ServiceResponse<CustomerDto>> createCustomer(@RequestBody CustomerDto customer) throws Exception{
		return customerService.createCustomer(customer);
	}

//	Fetch single or all customers with associated accounts
	@GetMapping("customers")
	public ResponseEntity<ServiceResponse<CustomerAccountDTO>> getAllCustomers(@RequestHeader(value = "id", required = false) UUID id) {
		return customerService.getAllCustomers(id);
	}
	
//	Update existing Customer
	@PutMapping("customer")
	public ResponseEntity<ServiceResponse<CustomerDto>> updateCustomer(@RequestHeader("id") UUID id, @RequestBody CustomerDto customer) {
		return customerService.updateCustomer(id, customer);
	}
	
//	Delete existing customer and associated accounts
	@DeleteMapping("customer")
	public ResponseEntity<ServiceResponse<Void>> deleteCustomer(@RequestHeader("id") UUID id) {
		return customerService.deleteCustomer(id);
	}
	
//	Used by account service to fetch single customer
	@GetMapping("fetchCustomer/{id}")
	public ResponseEntity<ServiceResponse<CustomerDto>> fetchCustomer(@PathVariable("id") UUID id) {
		logger.info("Instance running: " + applicationName + " on port " + serverPort);
		return customerService.fetchCustomer(id);
	}
	
}
