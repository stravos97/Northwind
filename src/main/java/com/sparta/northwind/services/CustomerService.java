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

    public void deleteCustomer(Customer customer)
    {
        customerRepository.delete(customer);
    }

    public void deleteCustomerById(String id){
        if(customerRepository.existsById(id)){
            customerRepository.deleteById(id);
        }else {
            throw new IllegalArgumentException("Can't delete Customer with ID " + id);
        }
    }

//    public Customer updateCustomer(Customer customer)
//    {
//        return customerRepository.save(customer);
//    }

    public Customer updateCustomerById(String id, Customer updatedCustomer){
        if(customerRepository.existsById(id)){
            updatedCustomer.setCustomerID(id); // Ensure the ID matches
            return customerRepository.save(updatedCustomer);
        }else {
            throw new IllegalArgumentException("Can't update Customer with ID " + id);
        }
    }
}

