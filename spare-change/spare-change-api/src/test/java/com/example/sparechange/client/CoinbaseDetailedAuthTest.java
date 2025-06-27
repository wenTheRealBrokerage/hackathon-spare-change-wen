package com.example.sparechange.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@SpringBootTest
public class CoinbaseDetailedAuthTest {
    
    @Test
    public void testDetailedAuth() {
        // Your credentials
        String apiKey = "40708ed4e26cbac94998ce2e20eb7f36";
        String apiSecret = "llgDL27Ve18ZWiyZk4O5wBKOiLZ1d2Vwb/+37BguxMVkiaoG9pMgqGW6EKTjPDA8szWRnLX8XeGyrUyIyG2MLg==";
        String passphrase = "tcfdjnrcwqkp";
        
        // Create WebClient with logging
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api-public.sandbox.exchange.coinbase.com")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .filter((request, next) -> {
                    System.out.println("=== Outgoing Request ===");
                    System.out.println("URI: " + request.url());
                    System.out.println("Method: " + request.method());
                    System.out.println("Headers:");
                    request.headers().forEach((name, values) -> 
                        System.out.println("  " + name + ": " + String.join(", ", values)));
                    System.out.println("======================");
                    return next.exchange(request);
                })
                .build();
        
        // Test different endpoints
        testEndpoint(webClient, apiKey, apiSecret, passphrase, "/time", "GET", "");
        testEndpoint(webClient, apiKey, apiSecret, passphrase, "/products", "GET", "");
        testEndpoint(webClient, apiKey, apiSecret, passphrase, "/accounts", "GET", "");
    }
    
    private void testEndpoint(WebClient webClient, String apiKey, String apiSecret, 
                             String passphrase, String path, String method, String body) {
        System.out.println("\n=== Testing endpoint: " + path + " ===");
        
        try {
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String signature = generateSignature(apiSecret, timestamp, method, path, body);
            
            String response = webClient.method(org.springframework.http.HttpMethod.valueOf(method))
                    .uri(path)
                    .header("CB-ACCESS-KEY", apiKey)
                    .header("CB-ACCESS-SIGN", signature)
                    .header("CB-ACCESS-TIMESTAMP", timestamp)
                    .header("CB-ACCESS-PASSPHRASE", passphrase)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "spare-change-api/1.0")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            System.out.println("✓ Success! Response: " + 
                              (response.length() > 200 ? response.substring(0, 200) + "..." : response));
                              
        } catch (Exception e) {
            System.err.println("✗ Failed: " + e.getMessage());
            if (e.getMessage().contains("401")) {
                System.err.println("Authentication failed. Check:");
                System.err.println("1. Are these Coinbase Exchange API keys (not regular Coinbase)?");
                System.err.println("2. Are they for the sandbox environment?");
                System.err.println("3. Is the passphrase exactly as shown when created?");
            }
        }
    }
    
    private String generateSignature(String secret, String timestamp, String method, String path, String body) {
        try {
            String what = timestamp + method.toUpperCase() + path + body;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            byte[] secretDecoded = Base64.getDecoder().decode(secret);
            SecretKeySpec secret_key = new SecretKeySpec(secretDecoded, "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(what.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }
}