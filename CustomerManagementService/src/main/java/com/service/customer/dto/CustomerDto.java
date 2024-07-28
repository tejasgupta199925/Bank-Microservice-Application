package com.service.customer.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerDto {

	private UUID id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private Integer age;
	
	private String gender;
	
	private String address;
	
	private LocalDate dob;
	
	private LocalDateTime createdTimestamp;

	public CustomerDto() {
		super();
	}

	public CustomerDto(UUID id, String firstName, String lastName, String email, Integer age, String gender,
			String address, LocalDate dob, LocalDateTime createdTimestamp) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.age = age;
		this.gender = gender;
		this.address = address;
		this.dob = dob;
		this.createdTimestamp = createdTimestamp;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public LocalDateTime getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	
}
