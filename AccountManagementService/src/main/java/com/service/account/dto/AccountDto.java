package com.service.account.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class AccountDto {

	private UUID id;
	
	private String accountNumber;
	
	private BigDecimal balance;
	
	private String accountType;
	
	private LocalDate accountOpeningDate;
	
	private LocalDateTime createdTimestamp;
	
	private LocalDateTime lastTransaction;
	
	private UUID customerId;

	public AccountDto() {
		super();
	}

	public AccountDto(UUID id, String accountNumber, BigDecimal balance, String accountType,
			LocalDate accountOpeningDate, LocalDateTime createdTimestamp, LocalDateTime lastTransaction,
			UUID customerId) {
		super();
		this.id = id;
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.accountType = accountType;
		this.accountOpeningDate = accountOpeningDate;
		this.createdTimestamp = createdTimestamp;
		this.lastTransaction = lastTransaction;
		this.customerId = customerId;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public LocalDate getAccountOpeningDate() {
		return accountOpeningDate;
	}

	public void setAccountOpeningDate(LocalDate accountOpeningDate) {
		this.accountOpeningDate = accountOpeningDate;
	}

	public LocalDateTime getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public LocalDateTime getLastTransaction() {
		return lastTransaction;
	}

	public void setLastTransaction(LocalDateTime lastTransaction) {
		this.lastTransaction = lastTransaction;
	}

	public UUID getCustomerId() {
		return customerId;
	}

	public void setCustomerId(UUID customerId) {
		this.customerId = customerId;
	}
	
}
