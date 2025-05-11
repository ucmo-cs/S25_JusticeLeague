package com.example.Project.repository;

import com.example.Project.domain.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    List<Url> findByCustomerUserId(String userId);
    void deleteById(Long id);
    void deleteAllByCustomerUserId(String userId);
}
