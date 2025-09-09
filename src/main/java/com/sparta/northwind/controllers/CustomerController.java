package com.sparta.northwind.controllers;

import com.sparta.northwind.dtos.CustomerDto;
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
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = service.getAllCustomer();
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Get customer by ID",
            description = "Retrieve a customer from the database using their unique ID")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@Size(min = 1, max = 5) @PathVariable String id) {
        CustomerDto customer = service.getCustomerByID(id);
        return customer != null ? ResponseEntity.ok(customer) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Add a new customer",
            description = "Create a new customer in the database")
    @PostMapping
    public ResponseEntity<CustomerDto> addCustomer(@Valid @RequestBody CustomerDto customer) {
        CustomerDto savedCustomer = service.createCustomer(customer);
        return ResponseEntity.status(201).body(savedCustomer);
    }

    @Operation(summary = "Update a customer",
            description = "Update an existing customer record in the database using their unique ID")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomerById(@Valid @RequestBody CustomerDto customerDto, @Size(min = 1, max = 5) @PathVariable String id) {

        /**
         *   Since CustomerDto is immutable (final fields), we cannot call setCustomerID(id).
         *   Instead, we create a new CustomerDto with the ID from the path parameter.
         *   This ensures the URL path is the "source of truth" for which resource to modify.
         *   
         *   In REST APIs, PUT /resource/{id} should always update the resource with that specific {id}, 
         *   regardless of what's in the request body. This prevents ID mismatch attacks.
         */
        CustomerDto customerWithPathId = new CustomerDto(
            id, // Use path parameter ID as source of truth
            customerDto.getCompanyName(),
            customerDto.getContactName(),
            customerDto.getCity()
        );

        CustomerDto updatedCustomer = service.updateCustomer(customerWithPathId);
        return updatedCustomer != null ? ResponseEntity.ok(updatedCustomer) : ResponseEntity.notFound().build();
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
