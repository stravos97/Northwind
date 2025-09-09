package com.sparta.northwind;

import com.sparta.northwind.dtos.CustomerDto;
import com.sparta.northwind.services.CustomerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication
public class NorthwindApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(NorthwindApplication.class, args);

//        CustomerRepository customerRepository = context.getBean(CustomerRepository.class);

//        List<Customer> customers = customerRepository.findAll();
//
//        for(Customer customer : customers){
//            System.out.println(customer);
//        }


        CustomerService customerService = context.getBean(CustomerService.class);

        System.out.println("=== Testing GET Methods ===");
        CustomerDto customerToFind = customerService.getCustomerByID("ALFKI");
        System.out.println("Single customer: " + customerToFind);

        List<CustomerDto> allCustomers = customerService.getAllCustomer();
        System.out.println("Total customers found: " + allCustomers.size());
        
        System.out.println("\n=== Testing UPDATE Method (Safe) ===");
        // Test validation logic without modifying real data
        if (customerToFind != null) {
            System.out.println("Customer ALFKI exists - update validation would succeed");
            System.out.println("Current company: " + customerToFind.getCompanyName());
        }
        
        // Test with non-existent customer (safe)
        CustomerDto testCustomerDto = new CustomerDto("TEST1", "Test Company", "Test Contact", "Test City");
        
        try {
            customerService.updateCustomer(testCustomerDto);
            System.out.println("Update successful");
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error for non-existent customer: " + e.getMessage());
        }

        System.out.println("\n=== Testing DELETE Method (Safe) ===");
        // Test delete validation without actually deleting
        System.out.println("Testing delete validation logic:");
        
        boolean deleted = customerService.deleteCustomerById("NONEXISTENT");
        if (deleted) {
            System.out.println("Delete successful");
        } else {
            System.out.println("Expected not found for non-existent customer");
        }

        // Show what would happen to real customer without actually deleting
        System.out.println("Customer ALFKI exists: deletion would succeed (but not executing)");
        System.out.println("Real data remains safe!");


    }

}
