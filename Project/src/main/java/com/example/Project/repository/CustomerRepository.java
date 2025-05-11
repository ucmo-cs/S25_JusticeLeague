package com.example.Project.repository;

import com.example.Project.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUserId(String userId);

    // ✅ Login query support: match userId + password
    Optional<Customer> findByUserIdAndPassword(String userId, String password);
}
