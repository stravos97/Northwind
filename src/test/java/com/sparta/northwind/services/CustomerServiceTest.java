package com.sparta.northwind.services;

import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private List<Customer> testCustomers;

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

        // Without this line, you'd only have individual customer objects, but getAllCustomers() returns a List<Customer>,
        // so the mock needs to return a list.
        testCustomers = Arrays.asList(testCustomer, customer2);
    }

    @Test
    void testGetAllCustomers() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(testCustomers);

        // Act
        List<Customer> result = customerService.getAllCustomer();

        // Assert
        assertEquals(2, result.size());
        assertEquals("TEST1", result.get(0).getCustomerID());
        assertEquals("TEST2", result.get(1).getCustomerID());
        verify(customerRepository).findAll();
    }

    @Test
    void testGetCustomerById_Success() {
        // Arrange
        when(customerRepository.findById("TEST1")).thenReturn(Optional.of(testCustomer));

        // Act
        Customer result = customerService.getCustomerByID("TEST1");

        // Assert
        assertNotNull(result);
        assertEquals("TEST1", result.getCustomerID());
        assertEquals("Test Company Ltd", result.getCompanyName());
        verify(customerRepository).findById("TEST1");
    }

    @Test
    void testGetCustomerById_NotFound() {
        // Arrange
        when(customerRepository.findById("DUMMY")).thenReturn(Optional.empty());

        // Act
        Customer result = customerService.getCustomerByID("DUMMY");

        // Assert
        assertNull(result);
        verify(customerRepository).findById("DUMMY");
    }

    @Test
    void testGetCustomerById_InvalidLength() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> customerService.getCustomerByID("TOOLONG123")
        );
        
        assertEquals("Can't have ID longer than 5 characters", exception.getMessage());

        //Prove that the repository method was never called because the validation caught the error first.
        verify(customerRepository, never()).findById(anyString());
    }

    @Test
    void testSaveCustomer() {
        // Arrange
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        // Act
        Customer result = customerService.saveCustomer(testCustomer);

        // Assert
        assertNotNull(result);
        assertEquals("TEST1", result.getCustomerID());
        verify(customerRepository).save(testCustomer);
    }

    @Test
    void testUpdateCustomer_Success() {
        // Arrange
        when(customerRepository.existsById("TEST1")).thenReturn(true);
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        // Act
        Customer result = customerService.updateCustomer(testCustomer);

        // Assert
        assertNotNull(result);
        assertEquals("TEST1", result.getCustomerID());
        verify(customerRepository).existsById("TEST1");
        verify(customerRepository).save(testCustomer);
    }

    @Test
    void testUpdateCustomer_NotFound() {
        // Arrange
        // Ensure the test customer has the missing ID
        testCustomer.setCustomerID("DUMMY");
        when(customerRepository.existsById("DUMMY")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> customerService.updateCustomer(testCustomer)
        );

        assertEquals("Customer with ID DUMMY does not exist.", exception.getMessage());
        verify(customerRepository).existsById("DUMMY");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomerById_Success() {
        // Arrange
        when(customerRepository.existsById("TEST1")).thenReturn(true);

        // Act
        boolean result = customerService.deleteCustomerById("TEST1");

        // Assert
        assertTrue(result);
        verify(customerRepository).existsById("TEST1");
        verify(customerRepository).deleteById("TEST1");
    }

    @Test
    void testDeleteCustomerById_NotFound() {
        // Arrange
        when(customerRepository.existsById("DUMMY")).thenReturn(false);

        // Act
        boolean result = customerService.deleteCustomerById("DUMMY");

        // Assert
        assertFalse(result);
        verify(customerRepository).existsById("DUMMY");
        verify(customerRepository, never()).deleteById(anyString());
    }
    
}
