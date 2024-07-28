package com.service.account.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.netflix.servo.util.Strings;
import com.service.account.dto.AccountDto;
import com.service.account.entities.Account;

public class UtilityMethods {

	public static String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            char randomChar = (char) (random.nextInt(26) + 'A');
            sb.append(randomChar);
        }

        for (int i = 0; i < 7; i++) {
            int randomDigit = random.nextInt(10);
            sb.append(randomDigit);
        }

        return sb.toString();
    }
	
	public static boolean checkNegativeAccountBalance(BigDecimal balance) {
		return balance.compareTo(BigDecimal.ZERO) < 0;
	}
	
	public static boolean checkExcessiveWithdrawalAmount(BigDecimal amount, BigDecimal balance) {
		return amount.compareTo(balance) > 0;
	}
	
	public static boolean isAccountTypeValid(String type) {
		if(Strings.isNullOrEmpty(type)) {
			return false;
		}
		if(type.equalsIgnoreCase("Saving") || type.equalsIgnoreCase("Checking")) {
			return true;
		}
		return false;
	}

	public static Account createEntityFromDto(AccountDto account) {
		return new Account(account.getAccountNumber(), account.getBalance(), account.getAccountType(), account.getAccountOpeningDate(), account.getCreatedTimestamp(), account.getLastTransaction(), account.getCustomerId());
	}
	
	public static AccountDto createDtoFromEntity(Account account){
		return new AccountDto(account.getId(), account.getAccountNumber(), account.getBalance(), account.getAccountType(), account.getAccountOpeningDate(), account.getCreatedTimestamp(), account.getLastTransaction(), account.getCustomerId());
	}
	
	public static List<AccountDto> createDtoFromEntityList(List<Account> accounts){
		List<AccountDto> dtos = new ArrayList<>();
		
		for(Account account : accounts) {
			dtos.add(new AccountDto(account.getId(), account.getAccountNumber(), account.getBalance(), account.getAccountType(), account.getAccountOpeningDate(), account.getCreatedTimestamp(), account.getLastTransaction(), account.getCustomerId()));
		}
		
		return dtos;
	}
	
}
