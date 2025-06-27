package com.example.sparechange.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@SpringBootTest
public class CoinbaseAuthDebugTest {
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Test
    public void testSimpleAuthRequest() {
        // Direct values from application.yaml
        String apiKey = "40708ed4e26cbac94998ce2e20eb7f36";
        String apiSecret = "llgDL27Ve18ZWiyZk4O5wBKOiLZ1d2Vwb/+37BguxMVkiaoG9pMgqGW6EKTjPDA8szWRnLX8XeGyrUyIyG2MLg==";
        String passphrase = "tcfdjnrcwqkp";
        
        WebClient webClient = webClientBuilder
                .baseUrl("https://api-public.sandbox.exchange.coinbase.com")
                .build();
        
        // Test with a simple GET request first
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String method = "GET";
        String requestPath = "/accounts";  // Try a simpler endpoint
        String body = "";
        
        // Generate signature
        String prehash = timestamp + method + requestPath + body;
        String signature = generateSignature(apiSecret, prehash);
        
        System.out.println("=== Testing Coinbase Auth ===");
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Prehash: " + prehash);
        System.out.println("Signature: " + signature);
        
        try {
            String response = webClient.get()
                    .uri(requestPath)
                    .header("CB-ACCESS-KEY", apiKey)
                    .header("CB-ACCESS-SIGN", signature)
                    .header("CB-ACCESS-TIMESTAMP", timestamp)
                    .header("CB-ACCESS-PASSPHRASE", passphrase)
                    .header("Accept", "application/json")
                    .header("User-Agent", "spare-change-api/1.0")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            System.out.println("Success! Response: " + response);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            // Try with different variations
            testWithVariations(webClient, apiKey, apiSecret, passphrase, timestamp, signature);
        }
    }
    
    private void testWithVariations(WebClient webClient, String apiKey, String apiSecret, 
                                   String passphrase, String timestamp, String signature) {
        System.out.println("\n=== Testing variations ===");
        
        // Test 1: Try without any spaces
        String trimmedPassphrase = passphrase.trim();
        System.out.println("Testing with trimmed passphrase: '" + trimmedPassphrase + "'");
        
        // Test 2: Try with lowercase/uppercase
        System.out.println("Testing with uppercase passphrase: '" + passphrase.toUpperCase() + "'");
        System.out.println("Testing with lowercase passphrase: '" + passphrase.toLowerCase() + "'");
        
        // Test 3: Check if it's a different API version
        System.out.println("\nNote: Make sure you're using Coinbase Exchange (Advanced Trade) API keys,");
        System.out.println("not the regular Coinbase API keys. They are different!");
        System.out.println("Exchange API keys are created at: https://exchange.coinbase.com/settings/api");
    }
    
    private String generateSignature(String secret, String message) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            byte[] secretDecoded = Base64.getDecoder().decode(secret);
            SecretKeySpec secretKey = new SecretKeySpec(secretDecoded, "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
}