package com.example.sparechange.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class CoinbaseUsdcConversionTest {
    
    private final String apiKey = "338d76f0ed9ebde87d0c5c847aa21508";
    private final String apiSecret = "Iw5f0WlsW1eVFqxGYXhBg+J4WsG651Rgj25xbhSxgW7GLI/8FhA3vYpnzhLR1zASEbyGdDtVWTjtjVKYkiLzaQ==";
    private final String passphrase = "66hvzqqxoqi9";
    
    @Test
    public void testUsdcToBtcConversion() throws Exception {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api-public.sandbox.exchange.coinbase.com")
                .build();
        
        ObjectMapper mapper = new ObjectMapper();
        
        // First, convert USDC to USD
        System.out.println("=== Step 1: Converting USDC to USD ===");
        System.out.println("Since we have 100,000 USDC and 0 USD, we need to sell USDC to get USD first");
        
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String method = "POST";
        String requestPath = "/orders";
        
        // Sell USDC to get USD
        Map<String, Object> sellOrder = new HashMap<>();
        sellOrder.put("type", "market");
        sellOrder.put("side", "sell");
        sellOrder.put("product_id", "USDC-USD");  // Sell USDC for USD
        sellOrder.put("size", "100.00");  // Sell 100 USDC
        
        String body = mapper.writeValueAsString(sellOrder);
        String signature = generateSignature(timestamp, method, requestPath, body);
        
        System.out.println("Attempting to sell USDC for USD...");
        System.out.println("Request body: " + body);
        
        try {
            String response = webClient.post()
                    .uri(requestPath)
                    .header("CB-ACCESS-KEY", apiKey)
                    .header("CB-ACCESS-SIGN", signature)
                    .header("CB-ACCESS-TIMESTAMP", timestamp)
                    .header("CB-ACCESS-PASSPHRASE", passphrase)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            System.out.println("✓ Successfully sold USDC! Response: " + response);
            
            // Wait a moment for the order to settle
            Thread.sleep(2000);
            
        } catch (WebClientResponseException e) {
            System.err.println("✗ Failed to sell USDC: " + e.getStatusCode());
            System.err.println("Error: " + e.getResponseBodyAsString());
        }
        
        // Check updated balances
        System.out.println("\n=== Step 2: Checking Updated Balances ===");
        checkBalances(webClient, mapper);
        
        // Now try to buy BTC with USD
        System.out.println("\n=== Step 3: Buying BTC with USD ===");
        
        timestamp = String.valueOf(Instant.now().getEpochSecond());
        
        Map<String, Object> buyOrder = new HashMap<>();
        buyOrder.put("type", "market");
        buyOrder.put("side", "buy");
        buyOrder.put("product_id", "BTC-USD");
        buyOrder.put("funds", "10.00");  // Buy $10 worth of BTC
        
        body = mapper.writeValueAsString(buyOrder);
        signature = generateSignature(timestamp, method, requestPath, body);
        
        System.out.println("Attempting to buy BTC with USD...");
        System.out.println("Request body: " + body);
        
        try {
            String response = webClient.post()
                    .uri(requestPath)
                    .header("CB-ACCESS-KEY", apiKey)
                    .header("CB-ACCESS-SIGN", signature)
                    .header("CB-ACCESS-TIMESTAMP", timestamp)
                    .header("CB-ACCESS-PASSPHRASE", passphrase)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            System.out.println("✓ Successfully bought BTC! Response: " + response);
            
            JsonNode orderResponse = mapper.readTree(response);
            System.out.println("Order ID: " + orderResponse.get("id").asText());
            
        } catch (WebClientResponseException e) {
            System.err.println("✗ Failed to buy BTC: " + e.getStatusCode());
            System.err.println("Error: " + e.getResponseBodyAsString());
        }
        
        // Final balance check
        Thread.sleep(2000);
        System.out.println("\n=== Step 4: Final Balance Check ===");
        checkBalances(webClient, mapper);
    }
    
    private void checkBalances(WebClient webClient, ObjectMapper mapper) {
        try {
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String method = "GET";
            String requestPath = "/accounts";
            String signature = generateSignature(timestamp, method, requestPath, "");
            
            String accountsJson = webClient.get()
                    .uri(requestPath)
                    .header("CB-ACCESS-KEY", apiKey)
                    .header("CB-ACCESS-SIGN", signature)
                    .header("CB-ACCESS-TIMESTAMP", timestamp)
                    .header("CB-ACCESS-PASSPHRASE", passphrase)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            JsonNode accounts = mapper.readTree(accountsJson);
            for (JsonNode account : accounts) {
                String currency = account.get("currency").asText();
                String available = account.get("available").asText();
                double availableNum = Double.parseDouble(available);
                
                if (availableNum > 0 || currency.equals("USD") || currency.equals("USDC") || currency.equals("BTC")) {
                    System.out.println("Account " + currency + ": available=" + available);
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking accounts: " + e.getMessage());
        }
    }
    
    private String generateSignature(String timestamp, String method, String requestPath, String body) {
        try {
            String prehash = timestamp + method.toUpperCase() + requestPath + body;
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            byte[] secretDecoded = Base64.getDecoder().decode(apiSecret);
            SecretKeySpec secretKey = new SecretKeySpec(secretDecoded, "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(prehash.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
}