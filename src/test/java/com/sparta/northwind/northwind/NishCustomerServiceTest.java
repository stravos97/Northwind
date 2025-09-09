package com.sparta.northwind.northwind;

import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.repository.CustomerRepository;
import com.sparta.northwind.services.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NishCustomerServiceTest {

    private final CustomerRepository mockRepository = Mockito.mock(CustomerRepository.class);
    private CustomerService sut = new CustomerService(mockRepository);

    // Dummies

    @Test
    @DisplayName("Ensure CustomerService is constructed correctly")
    public void constructServiceTest(){
        Assertions.assertInstanceOf(CustomerService.class, sut);
    }

    @Test
    @DisplayName("Constructor should throw exception with null repository")
    public void constructorWithNullRepositoryTest(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CustomerService(null));
    }

    // Stubs: Test specific behaviour

    @Test
    @DisplayName("Get all customers test")
    public void getAllCustomersTest(){
        // Arrange
        List<Customer> customers = new ArrayList<>();
        Customer customer1 = new Customer();
        customer1.setCustomerID("MANDA");
        Customer customer2 = new Customer();
        customer2.setCustomerID("WINDR");
        customers.add(customer1);
        customers.add(customer2);

        //// Define mock behaviour - create the STUB
        Mockito.when(mockRepository.findAll()).thenReturn(customers);

        // Act
        List<Customer> result = sut.getAllCustomer();

        // Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(result.get(0).getCustomerID(), "MANDA");
        Assertions.assertEquals(result.get(1).getCustomerID(), "WINDR");
    }


    @Test
    @DisplayName("Get Customer Happy Path")
    public void getCustomerByIdTest(){

        // Arrange
        Customer customer = new Customer();
        customer.setCustomerID("MANDA");
        customer.setCity("Birmingham");
        customer.setCompanyName("Sparta Global");
        customer.setContactName("Nish Mandal");

        Mockito.when(mockRepository.findById("MANDA")).thenReturn(Optional.of(customer));

        // Act

        Customer result = sut.getCustomerByID("MANDA");

        Assertions.assertNotNull(result, "Customer should not be null");
        Assertions.assertEquals("MANDA", result.getCustomerID(), "Customer ID should match");

    }


    @Test
    @DisplayName("Get Customer Unhappy Path")
    public void findCustomerUnhappyPathTests(){
        Mockito.when(mockRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        
        // Act
        Customer result = sut.getCustomerByID("ABCDE");
        
        // Assert - Your service returns null, not throws exception
        Assertions.assertNull(result, "Customer should be null when not found");
    }


    // ============================================================================
    // Additional Tests - Both Manual Mocking (above) and Annotation-Based Approaches
    // ============================================================================
    
    @Test
    @DisplayName("Create Customer Happy Path - Manual Mocking")
    public void createCustomerTest(){
        // Arrange
        Customer customerToSave = new Customer();
        customerToSave.setCustomerID("NEW01");
        customerToSave.setCompanyName("New Company");
        
        Customer expectedSavedCustomer = new Customer();
        expectedSavedCustomer.setCustomerID("NEW01");
        expectedSavedCustomer.setCompanyName("New Company");
        
        // Mock the repository to return false for existsById (customer doesn't exist)
        Mockito.when(mockRepository.existsById("NEW01")).thenReturn(false);
        Mockito.when(mockRepository.save(customerToSave)).thenReturn(expectedSavedCustomer);
        
        // Act - Note: Using createCustomer method as per your service changes
        Customer result = sut.createCustomer(customerToSave);
        
        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals("NEW01", result.getCustomerID());
        Assertions.assertEquals("New Company", result.getCompanyName());
        
        // Verify both methods were called
        Mockito.verify(mockRepository).existsById("NEW01");
        Mockito.verify(mockRepository).save(customerToSave);
    }
    
    @Test
    @DisplayName("Create Customer Sad Path - Customer Already Exists")
    public void createCustomerAlreadyExistsTest(){
        // Arrange
        Customer customerToSave = new Customer();
        customerToSave.setCustomerID("EXIST");
        
        // Mock the repository to return true for existsById (customer exists)
        Mockito.when(mockRepository.existsById("EXIST")).thenReturn(true);
        
        // Act & Assert - Should throw ResponseStatusException
        Assertions.assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> sut.createCustomer(customerToSave));
        
        // Verify existsById was called but save was NOT called
        Mockito.verify(mockRepository).existsById("EXIST");
        Mockito.verify(mockRepository, Mockito.never()).save(Mockito.any());
    }
    
    @Test
    @DisplayName("Update Customer Test - Manual Mocking")
    public void updateCustomerTest(){
        // Arrange
        Customer customerToUpdate = new Customer();
        customerToUpdate.setCustomerID("UPD01");
        customerToUpdate.setCompanyName("Updated Company");
        
        Mockito.when(mockRepository.save(customerToUpdate)).thenReturn(customerToUpdate);
        
        // Act
        Customer result = sut.updateCustomer(customerToUpdate);
        
        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals("UPD01", result.getCustomerID());
        Assertions.assertEquals("Updated Company", result.getCompanyName());
        
        // Verify save was called
        Mockito.verify(mockRepository).save(customerToUpdate);
    }
    
    @Test
    @DisplayName("Delete Customer Happy Path - Manual Mocking")
    public void deleteCustomerHappyPathTest(){
        // Arrange
        String customerId = "DEL01";
        Mockito.when(mockRepository.existsById(customerId)).thenReturn(true);
        
        // Act
        boolean result = sut.deleteCustomerById(customerId);
        
        // Assert
        Assertions.assertTrue(result);
        
        // Verify both methods were called
        Mockito.verify(mockRepository).existsById(customerId);
        Mockito.verify(mockRepository).deleteById(customerId);
    }
    
    @Test
    @DisplayName("Delete Customer Sad Path - Customer Not Found")
    public void deleteCustomerNotFoundTest(){
        // Arrange
        String customerId = "NOTFOUND";
        Mockito.when(mockRepository.existsById(customerId)).thenReturn(false);
        
        // Act
        boolean result = sut.deleteCustomerById(customerId);
        
        // Assert
        Assertions.assertFalse(result);
        
        // Verify existsById was called but deleteById was NOT called
        Mockito.verify(mockRepository).existsById(customerId);
        Mockito.verify(mockRepository, Mockito.never()).deleteById(Mockito.anyString());
    }

    // ============================================================================
    // LEARNING COMPARISON: Here's how the SAME tests would look with 
    // annotation-based approach (@Mock, @InjectMocks) like your existing file
    // ============================================================================
    
    /*
     * ANNOTATION-BASED APPROACH EXAMPLE (for comparison):
     * 
     * @ExtendWith(MockitoExtension.class)
     * class CustomerServiceAnnotationBasedTest {
     *     
     *     @Mock
     *     private CustomerRepository customerRepository;
     *     
     *     @InjectMocks
     *     private CustomerService customerService;
     *     
     *     @Test
     *     void createCustomerAnnotationStyle() {
     *         // Given
     *         Customer customer = new Customer();
     *         customer.setCustomerID("TEST");
     *         when(customerRepository.existsById("TEST")).thenReturn(false);
     *         when(customerRepository.save(customer)).thenReturn(customer);
     *         
     *         // When
     *         Customer result = customerService.createCustomer(customer);
     *         
     *         // Then
     *         assertNotNull(result);
     *         verify(customerRepository).save(customer);
     *     }
     * }
     * 
     * KEY DIFFERENCES:
     * 1. Manual: Explicit Mockito.mock() creation
     * 2. Annotation: @Mock annotation handles creation
     * 3. Manual: Manual constructor injection
     * 4. Annotation: @InjectMocks handles injection automatically
     * 5. Manual: Arrange/Act/Assert comments
     * 6. Annotation: Given/When/Then structure (BDD style)
     */
}
