package com.sparta.northwind.services;

import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.repository.CustomerRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomer(){
        return customerRepository.findAll();
    }

    public Customer getCustomerByID(String id){
        if(id.length()>5){
            throw  new IllegalArgumentException("Can't have ID longer than 5 characters");
        } else {
            return customerRepository.findById(id).orElse(null);
        }
    }

    public Customer saveCustomer(Customer customer){

        if(customer.getCustomerID().length()>5){
            throw  new IllegalArgumentException("Can't have ID longer than 5 characters");
        }

        if (customerRepository.existsById(customer.getCustomerID()))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer already exists");

        }

        return customerRepository.save(customer);

    }

    public boolean deleteCustomerById(String id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Customer updateCustomer(Customer customer) {
        if (customerRepository.existsById(customer.getCustomerID())) {
            return customerRepository.save(customer);
        } else {
            throw new IllegalArgumentException("Customer with ID " + customer.getCustomerID() + " does not exist.");
        }
    }
}

