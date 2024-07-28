package com.service.customer.dao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.service.customer.entities.Customer;

public interface CustomerDao extends JpaRepository<Customer, UUID> {

}
