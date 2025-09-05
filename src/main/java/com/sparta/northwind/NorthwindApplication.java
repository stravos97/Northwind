package com.sparta.northwind;

import com.sparta.northwind.entities.Customer;
import com.sparta.northwind.repository.CustomerRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication
public class NorthwindApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(NorthwindApplication.class, args);

        CustomerRepository customerRepository = context.getBean(CustomerRepository.class);

        List<Customer> customers = customerRepository.findAll();

        for(Customer customer : customers){
            System.out.println(customer);
        }
    }

}
