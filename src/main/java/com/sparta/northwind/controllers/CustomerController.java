package com.sparta.northwind.controllers;

import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController //@Controller and @ResponseBody
@RequestMapping("/customers")
@Validated
public class CustomerController {


    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }


    @Operation(summary = "Get all customers",
            description = "Retrieve a list of all customers")
    @GetMapping("/")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = service.getAllCustomer();
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Get customer by ID",
            description = "Retrieve a a customer from the database using their unique ID")

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@Size(min = 1, max = 5) @PathVariable String id) {

        Customer customer = service.getCustomerByID(id);
        return customer != null ? ResponseEntity.ok(customer) : ResponseEntity.notFound().build();

    }

    @Operation(summary = "Add a new customer",
            description = "Create a new customer in the database")
    @PostMapping
    public ResponseEntity<Customer> addCustomer(@Valid @RequestBody Customer customer) {
        Customer savedCustomer = service.createCustomer(customer);
        return ResponseEntity.status(201).body(savedCustomer);
    }

    @Operation(summary = "Update a customer",
            description = "Update an existing customer record in the database using their unique ID")
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomerById(@Valid @RequestBody Customer postRequestCustomer, @Size(min = 1, max = 5) @PathVariable String id) {


        /**
         *   Without postRequestCustomer.setCustomerID(id):
         *   - The system might try to update customer "HIJKL" instead of "ALFKI"
         *   - Or create a new customer with ID "HIJKL"
         *   - This breaks RESTful conventions and creates security vulnerabilities
         *
         *   With postRequestCustomer.setCustomerID(id):
         *   - Forces the customer object to use the ID from the URL path
         *   - Ensures you're always updating the customer specified in the URL
         *   - Prevents ID mismatch attacks
         *
         *   In REST APIs, PUT /resource/{id} should always update the resource with that specific {id}, regardless of what's in the request body. The URL path is the "source of truth" for which resource to modify.
         */


//        Customer existingCustomer = service.getCustomerByID(id);
//
//        if (existingCustomer != null) {
//            postRequestCustomer.setCustomerID(id); // Ensure ID matches path
//            Customer updatedCustomer = service.updateCustomer(postRequestCustomer);
//            return ResponseEntity.ok(updatedCustomer);
//
//        } else {
//            return ResponseEntity.notFound().build();
//        }

        postRequestCustomer.setCustomerID(id);
        Customer updatedCustomer = service.updateCustomer(postRequestCustomer);

        if (updatedCustomer != null)
        {
            return ResponseEntity.ok(updatedCustomer);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }


    }

    @Operation(summary = "Delete a customer",
            description = "Delete a customer in the database")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@Size(min = 1, max = 5) @PathVariable String id) {
        if (service.deleteCustomerById(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}
