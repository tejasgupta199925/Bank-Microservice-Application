package com.service.customer.service.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.service.customer.config.FeignConfig;
import com.service.customer.constants.ErrorCodeConstants;
import com.service.customer.constants.ErrorMessages;
import com.service.customer.dao.CustomerDao;
import com.service.customer.dto.AccountDto;
import com.service.customer.dto.CustomerAccountDTO;
import com.service.customer.dto.CustomerDto;
import com.service.customer.entities.Customer;
import com.service.customer.response.ServiceResponse;
import com.service.customer.service.CustomerService;
import com.service.customer.util.UtilityMethods;

import feign.FeignException;
import jakarta.transaction.Transactional;

@Service
public class CustomerServiceImplementation implements CustomerService {

	@Autowired
	private CustomerDao customerDao;
	
	@Autowired
	private FeignConfig feignConfig;
	
	private final Logger logger = LoggerFactory.getLogger(CustomerServiceImplementation.class);
	
	@Override
	public ResponseEntity<ServiceResponse<CustomerDto>> createCustomer(CustomerDto customer) throws Exception {
		try {
			customer.setCreatedTimestamp(LocalDateTime.now());			
			Customer entity = UtilityMethods.createEntityFromDto(customer);
			
			List<String> validationMessages = UtilityMethods.validateDetails(entity);
			if (!validationMessages.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(new ServiceResponse<>(null, String.join(", ", validationMessages), ErrorCodeConstants.VALIDATION_ERROR));
	        }
			
			Customer dbObject = customerDao.save(entity);
			customer.setId(dbObject.getId());
			logger.info("Customer object creation");
			return ResponseEntity.status(HttpStatus.CREATED).body(new ServiceResponse<CustomerDto>(customer, ErrorMessages.CUSTOMER_CREATED_SUCCESSFULLY, ErrorCodeConstants.CUSTOMER_CREATED));
		} catch(DataIntegrityViolationException e) {
			logger.error(ErrorCodeConstants.DATA_INTEGRITY_ERROR, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServiceResponse<>(null, ErrorMessages.DUPLICATE_EMAIL_ENTRY_NOW_ALLOWED, ErrorCodeConstants.DATA_INTEGRITY_ERROR));
		} catch(Exception e) {
			logger.error("Create Customer Service Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServiceResponse<>(null, ErrorMessages.AN_ERROR_OCCURRED + e.getMessage(), ErrorCodeConstants.GENERIC_ERROR));
		}
	}
	
	@Override
	public ResponseEntity<ServiceResponse<CustomerAccountDTO>> getAllCustomers(UUID id) {
		if(id == null) {
			List<Customer> customers = customerDao.findAll();
			
			if(customers.isEmpty()) {
				logger.warn("No Customer object: ", id);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ServiceResponse<CustomerAccountDTO>(null, null, ErrorMessages.NO_CUSTOMERS_FETCHED, ErrorCodeConstants.NO_CUSTOMER));
			}
			
			ResponseEntity<ServiceResponse<AccountDto>> accountServiceResponse = fetchAccountsForCustomer(null, false, true);
			
			if(accountServiceResponse.getStatusCode().is2xxSuccessful()) {
				List<CustomerAccountDTO> customerAccountList = new ArrayList<>();
				Map<UUID, List<AccountDto>> customerIdAccountMapping = new HashMap<>();
				
				for(AccountDto account : accountServiceResponse.getBody().getDataList()) {
					List<AccountDto> list = customerIdAccountMapping.getOrDefault(account.getCustomerId(), new ArrayList<>());
					list.add(account);
					customerIdAccountMapping.put(account.getCustomerId(), list);
				}
				
				for(Customer customer : customers) {
					List<AccountDto> list = new ArrayList<>();
					list.addAll(customerIdAccountMapping.getOrDefault(customer.getId(), new ArrayList<>()));
					customerAccountList.add(new CustomerAccountDTO(UtilityMethods.createDtoFromEntity(customer), list));
				}
				
				logger.info("All Customer Account Data Fetch");
				return ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<CustomerAccountDTO>(customerAccountList, null, ErrorMessages.CUSTOMERS_FETCHED, ErrorCodeConstants.DATA_FETCHED));
			} else {
				return ResponseEntity.status(accountServiceResponse.getStatusCode()).body(new ServiceResponse<CustomerAccountDTO>(null, accountServiceResponse.getBody().getMessage(), accountServiceResponse.getBody().getErrorCode()));
			}
		} else {
			Customer customer = customerDao.findById(id).orElse(null);
			
			if(customer == null) {
				logger.warn("No Customer object: ", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServiceResponse<CustomerAccountDTO>(null, null, ErrorMessages.NO_CUSTOMERS_FETCHED_WITH_ID+id, ErrorCodeConstants.NO_CUSTOMER));
			} else {
				ResponseEntity<ServiceResponse<AccountDto>> accountServiceResponse = fetchAccountsForCustomer(id, false, true);

				if(accountServiceResponse.getStatusCode().is2xxSuccessful()) {
					List<AccountDto> accounts = new ArrayList<>();
					accounts.addAll(accountServiceResponse.getBody().getDataList());
					CustomerAccountDTO details = new CustomerAccountDTO(UtilityMethods.createDtoFromEntity(customer), accounts);
					
					logger.info("Customer Account Data Fetch", id);
					return ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<CustomerAccountDTO>(null, details, ErrorMessages.CUSTOMERS_FETCHED_WITH_ID+id, ErrorCodeConstants.SPECIFIC_CUSTOMER_DETAILS));
				} else {
					return ResponseEntity.status(accountServiceResponse.getStatusCode()).body(new ServiceResponse<CustomerAccountDTO>(null, accountServiceResponse.getBody().getMessage(), accountServiceResponse.getBody().getErrorCode()));
				}
			}
		}
	}
	
	private ResponseEntity<ServiceResponse<AccountDto>> fetchAccountsForCustomer(UUID id, boolean accountId, boolean fetchAccount) {
		try {
			if(fetchAccount) {
				return feignConfig.fetchAccounts(id, accountId);
			} else {
				return feignConfig.deleteAccount(id, accountId);
			}
		} catch (FeignException.NotFound e) {
			logger.error("Data not found exception: ", e);
	        ServiceResponse<AccountDto> response = new ServiceResponse<>(null, ErrorMessages.ACCOUNT_NOT_FOUND, ErrorCodeConstants.ACCOUNT_NOT_FOUND);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    } catch (FeignException.FeignServerException e) {
	        logger.error("Feign Server Exception: ", e);
	    	ServiceResponse<AccountDto> response = new ServiceResponse<>(null, ErrorMessages.ACCOUNT_SERVICE_UNAVAILABLE, ErrorCodeConstants.SERVICE_UNAVAILABLE);
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	    } catch (FeignException.FeignClientException e) {
	    	logger.error("Feign Client Exception", e);
	        ServiceResponse<AccountDto> response = new ServiceResponse<>(null, ErrorMessages.INVALID_REQUEST, ErrorCodeConstants.INVALID_REQUEST);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    } catch (Exception e) {
	    	logger.error("Generic Exception: ", e);
	        ServiceResponse<AccountDto> response = new ServiceResponse<>(null, ErrorMessages.AN_ERROR_OCCURRED, ErrorCodeConstants.UNEXPECTED_ERROR);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	@Override
	public ResponseEntity<ServiceResponse<CustomerDto>> updateCustomer(UUID id, CustomerDto updatedCustomer) {
		try {
			Customer customer = customerDao.findById(id).orElse(null);
			
			if(customer == null) {
				logger.warn("No customer found: ", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(new ServiceResponse<>(null, ErrorMessages.NO_CUSTOMERS_FETCHED_WITH_ID+id, ErrorCodeConstants.NO_CUSTOMER));
			}
				        
	        customer = UtilityMethods.updateCustomerAttributes(customer, updatedCustomer);	        
	        List<String> validationMessages = UtilityMethods.validateDetails(customer);
	        if (!validationMessages.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(new ServiceResponse<>(null, String.join(", ", validationMessages), ErrorCodeConstants.VALIDATION_ERROR));
	        }
	        
	        Customer updatedDetails= customerDao.save(customer);
	        CustomerDto updatedObject = UtilityMethods.createDtoFromEntity(updatedDetails);
	        
	        logger.info("Customer update success");
	        return ResponseEntity.status(HttpStatus.OK)
	                .body(new ServiceResponse<>(updatedObject, ErrorMessages.CUSTOMER_UPDATE_SUCCESS, ErrorCodeConstants.CUSTOMER_UPDATED));
		} catch(DataIntegrityViolationException e) {
			logger.error(ErrorCodeConstants.DATA_INTEGRITY_ERROR, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServiceResponse<>(null, ErrorMessages.DUPLICATE_EMAIL_ENTRY_NOW_ALLOWED, ErrorCodeConstants.DATA_INTEGRITY_ERROR));
		} catch(Exception e) {
			logger.error("Update Customer Service Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServiceResponse<>(null, ErrorMessages.AN_ERROR_OCCURRED + e.getMessage(), ErrorCodeConstants.GENERIC_ERROR));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<ServiceResponse<Void>> deleteCustomer(UUID id) {
		try {
			Customer customer = customerDao.findById(id).orElse(null);
			
			if(customer == null) {
				logger.warn(ErrorCodeConstants.NO_CUSTOMER, id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServiceResponse<>(null, ErrorMessages.NO_CUSTOMERS_FETCHED_WITH_ID+id, ErrorCodeConstants.NO_CUSTOMER));
			}
			
			ResponseEntity<ServiceResponse<AccountDto>> accountServiceResponse = fetchAccountsForCustomer(id, false, false);
			
			if(!accountServiceResponse.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.status(accountServiceResponse.getStatusCode()).body(new ServiceResponse<Void>(null, accountServiceResponse.getBody().getMessage(), accountServiceResponse.getBody().getErrorCode()));
			}
			customerDao.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<>(null, ErrorMessages.CUSTOMER_ACCOUNT_DELETE_SUCCESS, ErrorCodeConstants.CUSTOMER_DELETED));			
		} catch(Exception e) {
			logger.error("Delete Customer Service Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServiceResponse<>(null, ErrorMessages.AN_ERROR_OCCURRED + e.getMessage(), ErrorCodeConstants.DELETE_OPERATION_ERROR));
		}
	}
	
	@Override
	public ResponseEntity<ServiceResponse<CustomerDto>> fetchCustomer(UUID id) {
		Customer customer = customerDao.findById(id).orElse(null);		
		if(customer == null) {
			logger.warn(ErrorCodeConstants.NO_CUSTOMER, id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServiceResponse<CustomerDto>(null, null, ErrorMessages.NO_CUSTOMERS_FETCHED_WITH_ID + id, ErrorCodeConstants.NO_CUSTOMER));
		} else {
			logger.info("Fetch Customer Details success");
			return ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<CustomerDto>(UtilityMethods.createDtoFromEntity(customer), ErrorMessages.CUSTOMERS_FETCHED_WITH_ID, ErrorCodeConstants.DATA_FETCHED));
		}		
	}

}
