package com.example.Project.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long url_id;

    private String name;

    private String url;

    private String ssl; // ✅ Certificate info from HTTPS

    private String resCode;

    @Lob // ✅ Allows long response headers
    private String resHead;

    private String sslPro; // ✅ SSL/TLS protocol + cipher

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    public Long getUrlId() {
        return url_id;
    }
}
