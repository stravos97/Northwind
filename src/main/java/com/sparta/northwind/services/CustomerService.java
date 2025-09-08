package com.sparta.northwind.services;

import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository)
    {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers()
    {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(String id)
    {
        if (id.length() > 5)
        {
            throw new IllegalArgumentException("Can't have ID longer than 5 characters");
        } else
        {
            return customerRepository.findById(id).orElse(null);
        }
    }

    public Customer saveCustomer(Customer customer)
    {
        return customerRepository.save(customer);
    }
}

