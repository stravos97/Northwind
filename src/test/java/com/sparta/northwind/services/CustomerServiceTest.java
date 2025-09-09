package com.sparta.northwind.services;

import com.sparta.northwind.dtos.CustomerDto;
import com.sparta.northwind.dtos.CustomerMapper;
import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private List<Customer> testCustomers;
    private CustomerDto testCustomerDto;
    private List<CustomerDto> testCustomerDtos;

    @BeforeEach
    void setUp() {
        // Using obviously fake test data to avoid confusion with real customer data
        testCustomer = new Customer();
        testCustomer.setCustomerID("TEST1");
        testCustomer.setCompanyName("Test Company Ltd");
        testCustomer.setContactName("Test User");
        
        Customer customer2 = new Customer();
        customer2.setCustomerID("TEST2");
        customer2.setCompanyName("Mock Corporation");
        customer2.setContactName("Mock Person");

        testCustomers = Arrays.asList(testCustomer, customer2);

        // Create corresponding DTOs for expected results
        testCustomerDto = new CustomerDto("TEST1", "Test Company Ltd", "Test User", null);
        CustomerDto customerDto2 = new CustomerDto("TEST2", "Mock Corporation", "Mock Person", null);
        testCustomerDtos = Arrays.asList(testCustomerDto, customerDto2);
    }

    @Test
    @DisplayName("Get all customers returns list of all customers from repository")
    void testGetAllCustomers() {
        // Given: repository contains a list of customer entities
        when(customerRepository.findAll()).thenReturn(testCustomers);
        // Mock the mapper to convert entities to DTOs
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDtos.get(0));
        when(customerMapper.toDto(testCustomers.get(1))).thenReturn(testCustomerDtos.get(1));

        // When: requesting all customers from service
        List<CustomerDto> actualCustomers = customerService.getAllCustomer();

        // Then: should return all customers as DTOs from service
        assertEquals(2, actualCustomers.size());
        assertEquals("TEST1", actualCustomers.get(0).getCustomerID());
        assertEquals("TEST2", actualCustomers.get(1).getCustomerID());
        assertEquals("Test Company Ltd", actualCustomers.get(0).getCompanyName());
        assertEquals("Mock Corporation", actualCustomers.get(1).getCompanyName());
        
        // Verify repository and mapper were called
        verify(customerRepository).findAll();
        verify(customerMapper).toDto(testCustomer);
        verify(customerMapper).toDto(testCustomers.get(1));
    }

    @Test
    @DisplayName("Get customer by ID returns customer when ID exists in repository")
    void testGetCustomerById_Success() {
        // Given: repository will return a customer for valid ID
        String customerId = "TEST1";
        Customer expectedCustomer = testCustomer;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(expectedCustomer));

        // When: requesting customer by ID
        Customer actualCustomer = customerService.getCustomerByID(customerId);

        // Then: should return the expected customer
        assertNotNull(actualCustomer);
        assertEquals(expectedCustomer.getCustomerID(), actualCustomer.getCustomerID());
        assertEquals(expectedCustomer.getCompanyName(), actualCustomer.getCompanyName());
        
        // Verify repository was called with correct ID
        verify(customerRepository).findById(customerId);
    }

    @Test
    @DisplayName("Get customer by ID returns null when ID does not exist in repository")
    void testGetCustomerById_NotFound() {
        // Given: repository will return empty for non-existent ID
        String nonExistentCustomerId = anyString();
        when(customerRepository.findById(nonExistentCustomerId)).thenReturn(Optional.empty());

        // When: requesting customer by non-existent ID
        Customer actualCustomer = customerService.getCustomerByID(nonExistentCustomerId);

        // Then: should return null
        assertNull(actualCustomer);
        
        // Verify repository was called with correct ID
        verify(customerRepository).findById(nonExistentCustomerId);
    }


    @Test
    @DisplayName("Save customer returns saved customer when repository save succeeds")
    void testCreateCustomer() {
        // Given: repository will save and return the customer
        Customer customerToSave = testCustomer;
        Customer expectedSavedCustomer = testCustomer;
        when(customerRepository.save(customerToSave)).thenReturn(expectedSavedCustomer);

        // When: saving a customer through service
        Customer actualSavedCustomer = customerService.createCustomer(customerToSave);

        // Then: should return the saved customer
        assertNotNull(actualSavedCustomer);
        assertEquals(expectedSavedCustomer.getCustomerID(), actualSavedCustomer.getCustomerID());
        
        // Verify repository save was called with correct customer
        verify(customerRepository).save(customerToSave);
    }

    @Test
    @DisplayName("Update customer returns updated customer when repository save succeeds")
    void testUpdateCustomer_Success() {
        // Given: a customer to update
        Customer customerToUpdate = testCustomer;
        Customer expectedUpdatedCustomer = testCustomer;
        when(customerRepository.save(customerToUpdate)).thenReturn(expectedUpdatedCustomer);

        // When: updating a customer
        Customer actualUpdatedCustomer = customerService.updateCustomer(customerToUpdate);

        // Then: should return the updated customer
        assertNotNull(actualUpdatedCustomer);
        assertEquals(expectedUpdatedCustomer.getCustomerID(), actualUpdatedCustomer.getCustomerID());

        // Verify ONLY save was called (no existence check in service)
        verify(customerRepository).save(customerToUpdate);
    }


    @Test
    @DisplayName("Delete customer by ID returns true when customer exists and is deleted successfully")
    void testDeleteCustomerById_Success() {
        // Given: customer exists in repository
        String customerId = "TEST1";
        when(customerRepository.existsById(customerId)).thenReturn(true);

        // When: deleting existing customer by ID
        boolean deletionResult = customerService.deleteCustomerById(customerId);

        // Then: should return true indicating successful deletion
        assertTrue(deletionResult);
        
        // Verify repository operations were called in correct order
        verify(customerRepository).existsById(customerId);
        verify(customerRepository).deleteById(customerId);
    }

    @Test
    @DisplayName("Delete customer by ID returns false when customer does not exist in repository")
    void testDeleteCustomerById_NotFound() {
        // Given: customer does not exist in repository
        String nonExistentCustomerId = anyString();
        when(customerRepository.existsById(nonExistentCustomerId)).thenReturn(false);

        // When: attempting to delete non-existent customer
        boolean deletionResult = customerService.deleteCustomerById(nonExistentCustomerId);

        // Then: should return false indicating no deletion occurred
        assertFalse(deletionResult);
        
        // Verify repository operations
        verify(customerRepository).existsById(nonExistentCustomerId);
        verify(customerRepository, never()).deleteById(nonExistentCustomerId);
    }
    
}
