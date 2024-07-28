package com.service.account.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.service.account.entities.Account;

public interface AccountDao extends JpaRepository<Account, UUID> {

	List<Account> findByCustomerId(UUID customerId);
	
	List<Account> deleteByCustomerId(UUID customerId);
}
