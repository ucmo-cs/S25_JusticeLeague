package com.example.Project.controller;

import com.example.Project.domain.DeleteRequest;
import com.example.Project.domain.Url;
import com.example.Project.domain.UrlRequest;
import com.example.Project.domain.UrlSaveRequest;
import com.example.Project.repository.UrlRepository;
import com.example.Project.service.UrlService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UrlController {

    @Autowired
    private UrlRepository urlRepository;

    private final UrlService urlService;

    // In URLController.java
    @PostMapping("/url")
    public ResponseEntity<?> save(@RequestBody UrlSaveRequest request) {
        Url url = new Url();
        url.setName(request.getName());
        url.setUrl(request.getUrl());

        //  Analyze the URL before saving it
        urlService.analyzeInline(url);

        // Save the URL and associate with the user
        Url savedUrl = urlService.create(url, request.getUserId());

        // Return the saved URL along with its analyzed data
        return new ResponseEntity<>(savedUrl, HttpStatus.CREATED);
    }

    @PutMapping("/urls")
    public ResponseEntity<List<Url>> updateUrls(@RequestBody List<Url> updatedUrls) {
        // Iterate over the list of URLs and update them one by one
        updatedUrls.forEach(url -> {
            Optional<Url> existingUrlOptional = urlRepository.findById(url.getUrlId());
            if (existingUrlOptional.isPresent()) {
                Url existingUrl = existingUrlOptional.get();
                existingUrl.setUrl(url.getUrl());
                urlRepository.save(existingUrl); // Save the updated URL
            }
        });

        return ResponseEntity.ok(updatedUrls); // Return the updated URLs
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeUrl(@RequestBody UrlRequest request) {
        try {
            Url url = new Url();
            url.setUrl(request.getUrl());

            // Analyze without saving
            urlService.analyzeInline(url);

            return ResponseEntity.ok(url);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error analyzing URL: " + e.getMessage());
        }
    }

    @PutMapping("/rescan")
    public ResponseEntity<List<Url>> rescanUrls(@RequestBody List<Long> urlIds) {
        // Fetch the URLs from the database using the instance of urlRepository
        List<Url> urls = urlRepository.findAllById(urlIds);

        // Rescan each URL
        urls.forEach(com.example.Project.util.UrlAnalyzer::analyzeUrl);

        // Save and return updated URLs
        List<Url> updatedUrls = urlRepository.saveAll(urls);
        return ResponseEntity.ok(updatedUrls);
    }

    @PutMapping("/url/{id}")
    public ResponseEntity<Url> updateUrl(@PathVariable Long id, @RequestBody Url updatedUrl) {
        Optional<Url> urlOptional = urlRepository.findById(id); // Find the URL by ID
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();

            // Update the fields you want to change (for example, the URL itself)
            url.setUrl(updatedUrl.getUrl());

            // Save the updated URL
            urlRepository.save(url);

            // Optionally, re-analyze the URL or update other properties
            com.example.Project.util.UrlAnalyzer.analyzeUrl(url);

            return ResponseEntity.ok(url); // Return the updated URL
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 if not found
        }
    }

    @GetMapping("/url/{userId}")
    public ResponseEntity<List<Url>> getUrlsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(urlService.getAllByUser(userId));
    }

    @DeleteMapping("/url/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Long id) {
        urlService.deleteUrlById(id);
        return ResponseEntity.ok("Deleted URL ID: " + id);
    }

    @DeleteMapping("/urs/user/{userId}")
    public ResponseEntity<?> deleteAllByUser(@PathVariable String userId) {
        urlService.deleteAllByUser(userId);
        return ResponseEntity.ok("Deleted all URLs for user: " + userId);
    }

    @DeleteMapping("/url")
    public ResponseEntity<List<Url>> deleteMultiple(@RequestBody DeleteRequest request) {
        urlService.deleteMultipleByIds(request.getUrlIds());

        List<Url> updatedUrls = urlService.getAllByUser(request.getUserId());

        return ResponseEntity.ok(updatedUrls);
    }
}
