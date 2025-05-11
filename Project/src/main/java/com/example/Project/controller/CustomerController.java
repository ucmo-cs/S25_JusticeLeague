package com.example.Project.controller;

import com.example.Project.domain.Customer;
import com.example.Project.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/user")
    public ResponseEntity<?> save(@RequestBody Customer customer) {
        System.out.println("Name : " + customer.getName());
        System.out.println("Password : " + customer.getPassword());

        return new ResponseEntity<>(customerService.create(customer), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Customer loginRequest) {
        Optional<Customer> user = customerService.login(loginRequest.getUserId(), loginRequest.getPassword());

        if (user.isPresent()) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
