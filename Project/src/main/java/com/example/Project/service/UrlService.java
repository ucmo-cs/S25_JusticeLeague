package com.example.Project.service;

import com.example.Project.domain.Customer;
import com.example.Project.domain.Url;
import com.example.Project.repository.CustomerRepository;
import com.example.Project.repository.UrlRepository;
import com.example.Project.util.UrlAnalyzer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final CustomerRepository customerRepository;

    public List<Url> getAllByUser(String userId) {
        return urlRepository.findByCustomerUserId(userId);
    }

    public Url create(Url url, String userId) {
        Customer customer = customerRepository.findByUserId(userId).orElse(null);
        if (customer != null) {
            url.setCustomer(customer);
        }

        // Analyze the URL before saving
        UrlAnalyzer.analyzeUrl(url);

        return urlRepository.save(url);
    }

    public void analyzeInline(Url url) {
        UrlAnalyzer.analyzeUrl(url); // For preview-only cases, e.g., in /analyze
    }

    public void deleteUrlById(Long id) {
        urlRepository.deleteById(id);
    }

    public void deleteAllByUser(String userId) {
        List<Url> urls = urlRepository.findByCustomerUserId(userId);
        urlRepository.deleteAll(urls);
    }
    public List<Url> updateUrls(List<Url> updatedUrls) {
        updatedUrls.forEach(updatedUrl -> {
            // Use getUrlId() to access the ID
            Url existingUrl = urlRepository.findById(updatedUrl.getUrlId())
                    .orElseThrow(() -> new RuntimeException("URL not found"));

            // Update the fields you want to change
            existingUrl.setUrl(updatedUrl.getUrl());
            existingUrl.setSsl(updatedUrl.getSsl());
            existingUrl.setResCode(updatedUrl.getResCode());
            existingUrl.setResHead(updatedUrl.getResHead());
            existingUrl.setSslPro(updatedUrl.getSslPro());

            // Re-analyze the URL if necessary
            UrlAnalyzer.analyzeUrl(existingUrl);

            // Save the updated URL back to the repository
            urlRepository.save(existingUrl);
        });

        return updatedUrls; // Return the updated URLs
    }
    public void deleteMultipleByIds(List<Long> urlIds) {
        // Ensure the URLs exist in the database before deleting
        List<Url> urlsToDelete = urlRepository.findAllById(urlIds);

        // Optionally, check if the list is empty before proceeding with delete
        if (!urlsToDelete.isEmpty()) {
            urlRepository.deleteAll(urlsToDelete);
        }
    }
}
