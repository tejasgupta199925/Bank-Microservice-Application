package com.service.account.dto;

public class CustomerAccountDTO {

	private CustomerDto customer;
	
	private AccountDto account;

	public CustomerAccountDTO() {
		super();
	}

	public CustomerAccountDTO(CustomerDto customer, AccountDto account) {
		super();
		this.customer = customer;
		this.account = account;
	}

	public CustomerDto getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDto customer) {
		this.customer = customer;
	}

	public AccountDto getAccount() {
		return account;
	}

	public void setAccount(AccountDto account) {
		this.account = account;
	}
	
}
