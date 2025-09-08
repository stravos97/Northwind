package com.sparta.northwind.controllers;

import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@RestController //@Controller and @ResponseBody
@RequestMapping("/customers")
public class CustomerController {


    private final CustomerService service;

    public CustomerController(CustomerService service){
        this.service = service;
    }


    @Operation(summary = "Get all customers",
                description = "Retrieve a list of all customers")
    @GetMapping(value = "/")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = service.getAllCustomer();
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Get customer by ID",
                description = "Retrieve a a customer from the database using their unique ID")

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id){
        Customer customer = service.getCustomerByID(id);
        if(customer != null){
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add a new customer",
                description = "Create a new customer in the database")
    @PostMapping
    public ResponseEntity<Customer> addCustomer(@Valid @RequestBody Customer customer){
        Customer savedCustomer = service.saveCustomer(customer);
        return ResponseEntity.status(201).body(savedCustomer);
    }

    @Operation(summary = "Update a customer",
                description = "Update an existing customer record in the database using their unique ID")
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomerById(@RequestBody Customer postRequestCustomer, @PathVariable String id)
    {

        try {

            Customer existingCustomer = service.getCustomerByID(id);

            if (existingCustomer == null)
            {
                return ResponseEntity.notFound().build();
            }

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
             *    In REST APIs, PUT /resource/{id} should always update the resource with that specific {id}, regardless of what's in the request body. The URL path is the "source of truth" for which resource to
             *   modify.
             */

            postRequestCustomer.setCustomerID(id); // Ensure ID matches path
            Customer updatedCustomer = service.updateCustomer(postRequestCustomer);
            return ResponseEntity.ok(updatedCustomer);

        }


        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(null); // Return 400 with the actual error
        }

    }

    @Operation(summary = "Delete a customer",
                description = "Delete a customer in the database")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id)
    {
          if( service.deleteCustomerById(id))
          {
              return ResponseEntity.noContent().build();
          } else
          {
              return ResponseEntity.notFound().build();
          }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}

