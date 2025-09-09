package com.sparta.northwind.services;

import com.sparta.northwind.dtos.CustomerDto;
import com.sparta.northwind.dtos.CustomerMapper;
import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper){
        if (customerRepository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }


    public List<CustomerDto> getAllCustomer() {
//        return customerRepository.findAll().stream().map(customerMapper::toDto).toList();

        List<Customer> customers = customerRepository.findAll();
        List<CustomerDto> customerDtos = new ArrayList<>();
        for (Customer customer : customers) {
            CustomerDto customerDto = customerMapper.toDto(customer);
            customerDtos.add(customerDto);
        }
        return customerDtos;
    }

    public CustomerDto getCustomerByID(String id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        return customer != null ? customerMapper.toDto(customer) : null;
    }

    public CustomerDto createCustomer(CustomerDto customerDto) {
        if (customerRepository.existsById(customerDto.getCustomerID())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer already exists");
        }

        Customer customer = customerMapper.toEntity(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDto(savedCustomer);
    }

    public boolean deleteCustomerById(String id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public CustomerDto updateCustomer(CustomerDto customerDto) {
        // Existence is validated in the controller before calling this method
        Customer customer = customerMapper.toEntity(customerDto);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toDto(updatedCustomer);
    }
}
