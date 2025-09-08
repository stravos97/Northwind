package com.sparta.northwind.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private Customer testCustomer1;
    private Customer testCustomer2;
    private List<Customer> customers;

    @BeforeEach
    void setUp() {
        testCustomer1 = new Customer();
        testCustomer1.setCustomerID("TEST1");
        testCustomer1.setCompanyName("Test Company Ltd");
        testCustomer1.setContactName("Test User");

        testCustomer2 = new Customer();
        testCustomer2.setCustomerID("TEST2");
        testCustomer2.setCompanyName("Mock Corporation");
        testCustomer2.setContactName("Mock Person");

        customers = Arrays.asList(testCustomer1, testCustomer2);
    }

    @Test
    @DisplayName("Get all customers returns OK with customer list")
    void getAllCustomers_returnsOkWithList() throws Exception {
        // Given: service will return a list of customers
        List<Customer> expectedCustomers = customers;
        when(customerService.getAllCustomer()).thenReturn(expectedCustomers);

        // Prepare the GET request
        MockHttpServletRequestBuilder request = get("/customers/");

        // When: requesting all customers
        ResultActions response = mockMvc.perform(request);
        
        // Then: should return 200 OK with JSON content
        response.andExpect(status().isOk());
        response.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // Verify response contains expected customer data
        response.andExpect(jsonPath("$", hasSize(2)));
        response.andExpect(jsonPath("$[0].customerID", is("TEST1")));
        response.andExpect(jsonPath("$[1].customerID", is("TEST2")));

        // Verify service was called
        verify(customerService).getAllCustomer();
    }

    @Test
    @DisplayName("Get customer by ID returns OK when customer exists")
    void getCustomerById_success_returnsOk() throws Exception {
        // Given: service will return a specific customer
        String customerId = "TEST1";
        Customer expectedCustomer = testCustomer1;
        when(customerService.getCustomerByID(customerId)).thenReturn(expectedCustomer);

        // Prepare the GET request
        MockHttpServletRequestBuilder request = get("/customers/" + customerId);

        // When: requesting a specific customer
        ResultActions response = mockMvc.perform(request);
        
        // Then: should return 200 OK with JSON content
        response.andExpect(status().isOk());
        response.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // Verify response contains expected customer data
        response.andExpect(jsonPath("$.customerID", is("TEST1")));
        response.andExpect(jsonPath("$.companyName", is("Test Company Ltd")));

        // Verify service was called with correct ID
        verify(customerService).getCustomerByID(customerId);
    }

    @Test
    @DisplayName("Get customer by ID returns 404 when customer not found")
    void getCustomerById_notFound_returns404() throws Exception {
        // Given: service will return null for non-existent customer
        String customerId = "DUMMY";
        when(customerService.getCustomerByID(customerId)).thenReturn(null);

        // Prepare the GET request
        MockHttpServletRequestBuilder request = get("/customers/" + customerId);

        // When: requesting a non-existent customer
        ResultActions response = mockMvc.perform(request);
        
        // Then: should return 404 Not Found
        response.andExpect(status().isNotFound());

        // Verify service was called with correct ID
        verify(customerService).getCustomerByID(customerId);
    }

    @Test
    @DisplayName("Get customer by ID returns 400 when customer ID is invalid length")
    void getCustomerById_invalidLength_returnsBadRequest() throws Exception {
        // Given: an invalid customer ID that's too long
        String invalidCustomerId = "TOOLONG";

        // Prepare the GET request
        MockHttpServletRequestBuilder request = get("/customers/" + invalidCustomerId);

        // When: sending a request with an invalid (too long) customer ID
        ResultActions response = mockMvc.perform(request);
        
        // Then: should return 400 Bad Request
        response.andExpect(status().isBadRequest());

        // Verify no service methods were called because validation fails first
        verifyNoInteractions(customerService);
    }

    @ParameterizedTest
    @CsvSource({
            "NEW01, New Co",
            "NEW02, Another Co",
            "NEW03, Yet Another Co"
    })
    @DisplayName("Add customer returns created with body")
    void addCustomer_returnsCreatedWithBody(String customerID, String companyName) throws Exception {

        // Given: a customer to save
        Customer customer = new Customer();
        customer.setCustomerID(customerID);
        customer.setCompanyName(companyName);

        // When: service saves the customer, it returns the same customer
        Customer expectedResult = customer;
        when(customerService.saveCustomer(any(Customer.class))).thenReturn(expectedResult);

        // Prepare the POST request
        String customerJson = objectMapper.writeValueAsString(customer);
        MockHttpServletRequestBuilder request = post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson);

        // Then: POST request should return 201 with the saved customer data
        ResultActions response = mockMvc.perform(request);
        
        // Verify response status and content type
        response.andExpect(status().isCreated());
        response.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // Verify response body contains correct data
        response.andExpect(jsonPath("$.customerID").value(is(customerID)));
        response.andExpect(jsonPath("$.companyName").value(is(companyName)));

        // Verify service was called
        verify(customerService).saveCustomer(any(Customer.class));
    }
}
