package com.sparta.northwind.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
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
    void getAllCustomers_returnsOkWithList() throws Exception {
        when(customerService.getAllCustomer()).thenReturn(customers);

        mockMvc.perform(get("/customers/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].customerID", is("TEST1")))
                .andExpect(jsonPath("$[1].customerID", is("TEST2")));

        verify(customerService).getAllCustomer();
    }

    @Test
    void getCustomerById_success_returnsOk() throws Exception {
        when(customerService.getCustomerByID("TEST1")).thenReturn(testCustomer1);

        mockMvc.perform(get("/customers/TEST1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerID", is("TEST1")))
                .andExpect(jsonPath("$.companyName", is("Test Company Ltd")));

        verify(customerService).getCustomerByID("TEST1");
    }

    @Test
    void getCustomerById_notFound_returns404() throws Exception {
        when(customerService.getCustomerByID("DUMMY")).thenReturn(null);

        mockMvc.perform(get("/customers/DUMMY"))
                .andExpect(status().isNotFound());

        verify(customerService).getCustomerByID("DUMMY");
    }

    @Test
    void getCustomerById_invalidLength_returnsServerError() throws Exception {
        when(customerService.getCustomerByID("TOOLONG"))
                .thenThrow(new IllegalArgumentException("Can't have ID longer than 5 characters"));

        mockMvc.perform(get("/customers/TOOLONG"))
                .andExpect(status().isInternalServerError());

        verify(customerService).getCustomerByID("TOOLONG");
    }

    @Test
    void addCustomer_returnsCreatedWithBody() throws Exception {
        Customer input = new Customer();
        input.setCustomerID("NEW01");
        input.setCompanyName("New Co");

        Customer saved = new Customer();
        saved.setCustomerID("NEW01");
        saved.setCompanyName("New Co");

        when(customerService.saveCustomer(any(Customer.class))).thenReturn(saved);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerID", is("NEW01")))
                .andExpect(jsonPath("$.companyName", is("New Co")));

        verify(customerService).saveCustomer(any(Customer.class));
    }
}

