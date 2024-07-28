package com.service.account.service.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.service.account.config.FeignConfig;
import com.service.account.constants.ErrorCodeConstants;
import com.service.account.constants.ErrorMessages;
import com.service.account.dao.AccountDao;
import com.service.account.dto.AccountDto;
import com.service.account.dto.CustomerAccountDTO;
import com.service.account.dto.CustomerDto;
import com.service.account.entities.Account;
import com.service.account.response.ServiceResponse;
import com.service.account.service.AccountService;
import com.service.account.util.UtilityMethods;

import feign.FeignException;
import jakarta.transaction.Transactional;

@Service
public class AccountServiceImplementation implements AccountService {

	@Autowired
	private FeignConfig feignConfig;
	
	@Autowired
	private AccountDao accountDao;
	
	private final Logger logger = LoggerFactory.getLogger(AccountServiceImplementation.class);
	
	private static final String DEPOSIT = "Deposit";
	
	private static final String WITHDRAWAL = "Withdrawal";
	
	@Override
	public ResponseEntity<ServiceResponse<AccountDto>> createAccount(AccountDto account) {			
		try {
			ResponseEntity<ServiceResponse<CustomerDto>> customerServiceResponse = fetchCustomerForAccount(account.getCustomerId());
			
			if(customerServiceResponse.getStatusCode().is2xxSuccessful()) {
				if(account.getBalance() == null) {
					account.setBalance(BigDecimal.ZERO);
				}
				else if(UtilityMethods.checkNegativeAccountBalance(account.getBalance())) {
					logger.warn("Negative account balance input: ", account.getBalance());
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResponse<AccountDto>(null, ErrorMessages.NEGATIVE_BALANCE, ErrorCodeConstants.INVALID_REQUEST));
				}
				if(!UtilityMethods.isAccountTypeValid(account.getAccountType())) {
					logger.warn("Invalid Account Type: ", account.getAccountType());
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResponse<AccountDto>(null, ErrorMessages.INVALID_ACCOUNT_TYPE, ErrorCodeConstants.INVALID_REQUEST));
				}
				
				account.setAccountNumber(UtilityMethods.generateAccountNumber());
				account.setCreatedTimestamp(LocalDateTime.now());
				account.setAccountOpeningDate(LocalDate.now());
				account.setLastTransaction(account.getCreatedTimestamp());
				
				Account entity = UtilityMethods.createEntityFromDto(account);
				Account object = accountDao.save(entity);
				account.setId(object.getId());
				
				logger.info("Account Create success");
				return ResponseEntity.status(HttpStatus.CREATED).body(new ServiceResponse<AccountDto>(account, ErrorMessages.ACCOUNT_CREATION_SUCCESS, ErrorCodeConstants.ACCOUNT_CREATED));
			} else {
				return ResponseEntity.status(customerServiceResponse.getStatusCode()).body(new ServiceResponse<AccountDto>(null, customerServiceResponse.getBody().getMessage(), customerServiceResponse.getBody().getErrorCode()));
			}
		} catch(DataIntegrityViolationException e) {
			logger.error(ErrorCodeConstants.DATA_INTEGRITY_ERROR);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServiceResponse<>(null, ErrorMessages.DUPLICATE_ACCOUNT_NUMBER, ErrorCodeConstants.DATA_INTEGRITY_ERROR));
		}
		catch(Exception e) {
			logger.error("Create Account service error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServiceResponse<>(null, ErrorMessages.ERROR_OCCURED + e.getMessage(), ErrorCodeConstants.GENERIC_ERROR));
		}
	}
	
	public ResponseEntity<ServiceResponse<AccountDto>> fetchAccounts(UUID id, boolean accountId) {
		List<Account> accounts = new ArrayList<>();
		if(id == null) {
			accounts = accountDao.findAll();
		} else {
			Account account = null;
			if(accountId) {
				account = accountDao.findById(id).orElse(null);

				if(account!=null) {
					accounts.add(account);
				}
			} else {
				accounts.addAll(accountDao.findByCustomerId(id));
			}
		}
		logger.info("Fetch Accounts service success");
		return ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<AccountDto>(UtilityMethods.createDtoFromEntityList(accounts), null, ErrorMessages.ACCOUNTS_FETCHED, ErrorCodeConstants.ACCOUNTS_FETCHED));
	}
	
	@Override
	public ResponseEntity<ServiceResponse<AccountDto>> updateAccountBalance(UUID id, BigDecimal amount, boolean deposit) {
		Account account = accountDao.findById(id).orElse(null);
		
		if(account == null) {
			logger.warn("No Account: ", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServiceResponse<AccountDto>(null, null, ErrorMessages.NO_ACCOUNTS_WITH_ID+id, ErrorCodeConstants.NO_ACCOUNT));
		}
		
		ResponseEntity<ServiceResponse<CustomerDto>> customerServiceResponse = fetchCustomerForAccount(account.getCustomerId());		
		if(!customerServiceResponse.getStatusCode().is2xxSuccessful()) {
			return ResponseEntity.status(customerServiceResponse.getStatusCode()).body(new ServiceResponse<AccountDto>(null, customerServiceResponse.getBody().getMessage(), customerServiceResponse.getBody().getErrorCode()));
		}
		if(UtilityMethods.checkNegativeAccountBalance(amount)) {
			logger.warn("Negative amount entered for transaction: "+amount);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResponse<AccountDto>(null, ErrorMessages.NEGATIVE_AMOUNT, ErrorCodeConstants.INVALID_REQUEST));
        }
		if(!deposit && UtilityMethods.checkExcessiveWithdrawalAmount(amount, account.getBalance())) {
			logger.warn("Insufficient account balance for withdrawal: "+amount);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ServiceResponse<AccountDto>(null, ErrorMessages.EXCESSIVE_WITHDRAWAL_AMOUNT, ErrorCodeConstants.INVALID_REQUEST));
        }
		
        account.setBalance(deposit ? account.getBalance().add(amount) : account.getBalance().subtract(amount));
        account.setLastTransaction(LocalDateTime.now());
        accountDao.save(account);
        
        String operation = deposit ? DEPOSIT : WITHDRAWAL;
        logger.info("Account transaction success");
		return ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<AccountDto>(UtilityMethods.createDtoFromEntity(account), operation+ErrorMessages.SUCCESSFUL, ErrorCodeConstants.BALANCE_UPDATED));
	}

	@Override
	@Transactional
	public ResponseEntity<ServiceResponse<AccountDto>> deleteAccount(UUID id, boolean accountId) {		
		try {
			if(accountId) {
				Account account = accountDao.findById(id).orElse(null);
				if(account == null) {
					logger.warn("No Account: ", id);
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServiceResponse<AccountDto>(null, null, ErrorMessages.NO_ACCOUNTS_WITH_ID+id, ErrorCodeConstants.NO_ACCOUNT));
				}
				
				accountDao.delete(account);
			} else {
//				Called by customer service while deleting customers only
				List<Account> accounts = accountDao.deleteByCustomerId(id);
			}
			
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.info("Delete account service ");
			return ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<>(null, ErrorMessages.ACCOUNT_DELETE_SUCCESS, ErrorCodeConstants.ACCOUNT_DELETED));
		} catch(Exception e) {
			logger.error("Delete Account service error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServiceResponse<>(null, ErrorMessages.ERROR_OCCURED + e.getMessage(), ErrorCodeConstants.DELETE_OPERATION_ERROR));
		}

	}

	@Override
	public ResponseEntity<ServiceResponse<CustomerAccountDTO>> fetchAccountCustomerInfo(UUID id) {
		Account account = accountDao.findById(id).orElse(null);
		
		if(account == null) {
			logger.warn(ErrorCodeConstants.NO_ACCOUNT, id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServiceResponse<CustomerAccountDTO>(null, null, ErrorMessages.NO_ACCOUNTS_WITH_ID+id, ErrorCodeConstants.NO_ACCOUNT));
		}

		ResponseEntity<ServiceResponse<CustomerDto>> customerServiceResponse = fetchCustomerForAccount(account.getCustomerId());

		if(customerServiceResponse.getStatusCode().is2xxSuccessful()) {
			AccountDto dto = UtilityMethods.createDtoFromEntity(account);
			CustomerAccountDTO details = new CustomerAccountDTO(customerServiceResponse.getBody().getData(), dto);
			return ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<CustomerAccountDTO>(details, ErrorMessages.CUSTOMER_DETAILS_FETCHED, ErrorCodeConstants.DATA_FOUND));
		} else {
			return ResponseEntity.status(customerServiceResponse.getStatusCode()).body(new ServiceResponse<CustomerAccountDTO>(null, customerServiceResponse.getBody().getMessage(), customerServiceResponse.getBody().getErrorCode()));
		}
	}
	
	public ResponseEntity<ServiceResponse<CustomerDto>> fetchCustomerForAccount(UUID id) {
		try {
			return feignConfig.fetchCustomer(id);
		} catch (FeignException.NotFound e) {
			logger.error("Feign not found exception: ", e);
	        ServiceResponse<CustomerDto> response = new ServiceResponse<>(null, ErrorMessages.CUSTOMER_NOT_FOUND, ErrorCodeConstants.NO_CUSTOMER);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    } catch (FeignException.FeignServerException e) {
	    	logger.error("Feign server exception: ", e);
	    	ServiceResponse<CustomerDto> response = new ServiceResponse<>(null, ErrorMessages.CUSTOMER_SERVICE_UNAVAILABLE, ErrorCodeConstants.SERVICE_UNAVAILABLE);
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	    } catch (FeignException.FeignClientException e) {
	    	logger.error("Feign client exception: ", e);
	        ServiceResponse<CustomerDto> response = new ServiceResponse<>(null, ErrorMessages.INVALID_CUSTOMER_SERVICE_REQUEST, ErrorCodeConstants.INVALID_REQUEST);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    } catch (Exception e) {
	    	logger.error("Generic exception: ", e);
	        ServiceResponse<CustomerDto> response = new ServiceResponse<>(null, ErrorMessages.ERROR_OCCURED, ErrorCodeConstants.UNEXPECTED_ERROR);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

}
