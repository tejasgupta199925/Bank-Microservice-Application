package com.service.account.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	@Column(name="account_number", unique = true)
	private String accountNumber;
	
	private BigDecimal balance;
	
	@Column(name="account_type")
	private String accountType;
	
	@Column(name = "date_of_opening")
	private LocalDate accountOpeningDate;
	
	@Column(name = "created_timestamp")
	private LocalDateTime createdTimestamp;
	
	@Column(name = "last_transaction")
	private LocalDateTime lastTransaction;
	
	@Column(name="customer_id")
	private UUID customerId;

	public Account() {
		super();
	}

	public Account(String accountNumber, BigDecimal balance, String accountType, LocalDate accountOpeningDate,
			LocalDateTime createdTimestamp, LocalDateTime lastTransaction, UUID customerId) {
		super();
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
