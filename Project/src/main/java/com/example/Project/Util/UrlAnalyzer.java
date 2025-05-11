package com.example.Project.util;

import com.example.Project.domain.Url;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

public class UrlAnalyzer {

    public static void analyzeUrl(Url urlEntity) {
        try {
            URL url = new URL(urlEntity.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            urlEntity.setResCode(String.valueOf(connection.getResponseCode()));
            urlEntity.setResHead(formatHeaders(connection.getHeaderFields()));

            if (connection instanceof HttpsURLConnection) {
                analyzeHttpsConnection((HttpsURLConnection) connection, urlEntity);
            }

            connection.disconnect();
        } catch (Exception e) {
            handleAnalysisError(urlEntity, e);
        }
    }

    private static void analyzeHttpsConnection(HttpsURLConnection connection, Url urlEntity) {
        try {
            Optional<SSLSession> ssLSessionOpt = connection.getSSLSession();
            if (ssLSessionOpt.isPresent()) {
                SSLSession ssLSession = ssLSessionOpt.get();
                urlEntity.setSslPro(ssLSession.getProtocol() + " / " + ssLSession.getCipherSuite());

                Certificate[] certs = ssLSession.getPeerCertificates();
                if (certs.length > 0 && certs[0] instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) certs[0];
                    urlEntity.setSsl(String.format(
                            "Issuer: %s%nSubject: %s%nValid Until: %s",
                            x509Cert.getIssuerX500Principal().getName(),
                            x509Cert.getSubjectX500Principal().getName(),
                            x509Cert.getNotAfter()
                    ));
                }
            }
        } catch (Exception e) {
            urlEntity.setSsl("Error: " + e.getMessage());
        }
    }

    private static String formatHeaders(Map<String, List<String>> headers) {
        StringBuilder sb = new StringBuilder();
        headers.entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .forEach(entry -> sb.append(entry.getKey())
                        .append(": ")
                        .append(String.join(", ", entry.getValue()))
                        .append("\n"));
        return sb.toString();
    }

    private static void handleAnalysisError(Url urlEntity, Exception e) {
        urlEntity.setResCode("ERROR");
        urlEntity.setResHead("Analysis failed: " + e.getMessage());
        urlEntity.setSsl("Not available");
        urlEntity.setSslPro("Not available");
    }
}
