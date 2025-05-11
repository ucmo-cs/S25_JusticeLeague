package com.example.Project.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateInfoDTO {
    private String subject;
    private String issuer;
    private String validFrom;
    private String validTo;
    private String serialNumber;
    private String signatureAlgorithm;
}
