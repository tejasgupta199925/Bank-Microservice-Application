package com.service.customer.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.service.customer.dto.CustomerDto;
import com.service.customer.entities.Customer;

public class UtilityMethods {
	
	private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
	private static final List<String> VALID_GENDERS = List.of("male", "female", "other");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

	public static List<String> validateDetails(Customer customer) {
		List<String> validationMessages = new ArrayList<>();
		
		if (customer.getFirstName() == null || customer.getFirstName().isEmpty()) {
            validationMessages.add("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().isEmpty()) {
            validationMessages.add("Last name is required");
        }
        if (customer.getAge() == null || customer.getDob() == null) {
            validationMessages.add("Age and Date of Birth are required");
        } else if (calculateAge(customer.getDob()) != customer.getAge()) {
            validationMessages.add("Age does not match Date of Birth");
        } else if(customer.getAge() > 100 || customer.getAge() < 5) {
        	validationMessages.add("Enter proper age (5-100)");
        }        
        if (customer.getEmail() == null || !EMAIL_PATTERN.matcher(customer.getEmail()).matches()) {
            validationMessages.add("Valid Email required");
        }
        if(customer.getAddress() == null || customer.getAddress().isEmpty()) {
        	validationMessages.add("Address is required");
        }
        if(customer.getGender() == null || customer.getGender().isEmpty()) {
        	validationMessages.add("Gender is required");
        } else if(!VALID_GENDERS.contains(customer.getGender().toLowerCase())) {
        	validationMessages.add("Enter valid Gender (Male, Female, Other)");
        }
		
		return validationMessages;
	}
	
	private static int calculateAge(LocalDate dob) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(dob, currentDate).getYears();
    }

	public static Customer createEntityFromDto(CustomerDto dto) {
		return new Customer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getAge(), dto.getGender(), dto.getAddress(), dto.getDob(), dto.getCreatedTimestamp());
	}

	public static CustomerDto createDtoFromEntity(Customer c) {
		return new CustomerDto(c.getId(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getAge(), c.getGender(), c.getAddress(), c.getDob(), c.getCreatedTimestamp());
	}
	
	public static Customer updateCustomerAttributes(Customer customer, CustomerDto dto) {
		if (dto.getFirstName() != null && !dto.getFirstName().isEmpty()) {
			customer.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().isEmpty()) {
        	customer.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
        	customer.setEmail(dto.getEmail());
        }
        if (dto.getAge() != null) {
			customer.setAge(dto.getAge());
        }
        if (dto.getGender() != null && !dto.getGender().isEmpty()) {
        	customer.setGender(dto.getGender());
        }
        if (dto.getAddress() != null && !dto.getAddress().isEmpty()) {
        	customer.setAddress(dto.getAddress());
        }
        if (dto.getDob() != null) {
        	customer.setDob(dto.getDob());
        }
        
		return customer;
	}
}
