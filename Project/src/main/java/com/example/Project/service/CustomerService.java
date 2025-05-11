package com.example.Project.service;

import com.example.Project.domain.Customer;
import com.example.Project.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> login(String userId, String password) {
        return customerRepository.findByUserIdAndPassword(userId, password);
    }
}
