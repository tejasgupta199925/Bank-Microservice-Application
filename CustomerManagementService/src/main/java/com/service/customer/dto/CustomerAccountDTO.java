package com.service.customer.dto;

import java.util.List;

public class CustomerAccountDTO {
	
	private CustomerDto customer;
	
	private List<AccountDto> accounts;

	public CustomerAccountDTO() {
		super();
	}

	public CustomerAccountDTO(CustomerDto customer, List<AccountDto> accounts) {
		super();
		this.customer = customer;
		this.accounts = accounts;
	}

	public CustomerDto getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDto customer) {
		this.customer = customer;
	}

	public List<AccountDto> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountDto> accounts) {
		this.accounts = accounts;
	}

}
