package com.sparta.northwind;

import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.repository.CustomerRepository;
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

        Customer customerToFind = customerService.getCustomerById("1");
        List<Customer> customers = customerService.getAllCustomers();
        System.out.println("For single customer: " + customerToFind);

        for (Customer customer : customers) {
            System.out.println(customer);
        }


    }

}
