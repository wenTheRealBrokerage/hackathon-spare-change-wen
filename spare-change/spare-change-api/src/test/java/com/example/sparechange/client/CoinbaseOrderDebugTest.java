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
public class CoinbaseOrderDebugTest {
    
    private final String apiKey = "338d76f0ed9ebde87d0c5c847aa21508";
    private final String apiSecret = "Iw5f0WlsW1eVFqxGYXhBg+J4WsG651Rgj25xbhSxgW7GLI/8FhA3vYpnzhLR1zASEbyGdDtVWTjtjVKYkiLzaQ==";
    private final String passphrase = "66hvzqqxoqi9";
    
    @Test
    public void debugOrderPlacement() throws Exception {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api-public.sandbox.exchange.coinbase.com")
                .build();
        
        ObjectMapper mapper = new ObjectMapper();
        
        // First, list all available products
        System.out.println("=== Step 1: Listing All Available Products ===");
        try {
            String productsJson = webClient.get()
                    .uri("/products")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            JsonNode products = mapper.readTree(productsJson);
            System.out.println("Total products: " + products.size());
            
            for (JsonNode product : products) {
                String id = product.get("id").asText();
                String status = product.has("status") ? product.get("status").asText() : "unknown";
                String tradingDisabled = product.has("trading_disabled") ? product.get("trading_disabled").asText() : "unknown";
                System.out.println("Product: " + id + ", Status: " + status + ", Trading Disabled: " + tradingDisabled);
                
                if (id.startsWith("BTC-") || id.startsWith("USDC-")) {
                    System.out.println("  -> Found relevant product: " + id);
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing products: " + e.getMessage());
        }
        
        // Check accounts
        System.out.println("\n=== Step 2: Checking Account Balances ===");
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
                String balance = account.get("balance").asText();
                String available = account.get("available").asText();
                System.out.println("Account " + currency + ": balance=" + balance + ", available=" + available);
            }
        } catch (Exception e) {
            System.err.println("Error checking accounts: " + e.getMessage());
        }
        
        // Try different order formats
        System.out.println("\n=== Step 3: Testing Order Placement ===");
        
        // Test 1: Market order with funds
        testOrder(webClient, mapper, "BTC-USD", "market", "buy", "funds", "10.00");
        
        // Test 2: Market order with size
        testOrder(webClient, mapper, "BTC-USD", "market", "buy", "size", "0.0001");
        
        // Test 3: Try USDC-USD
        testOrder(webClient, mapper, "USDC-USD", "market", "buy", "funds", "10.00");
    }
    
    private void testOrder(WebClient webClient, ObjectMapper mapper, String productId, 
                          String type, String side, String quantityType, String quantity) {
        System.out.println("\n--- Testing order: " + productId + " " + type + " " + side + " " + quantityType + "=" + quantity);
        
        try {
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String method = "POST";
            String requestPath = "/orders";
            
            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("type", type);
            orderRequest.put("side", side);
            orderRequest.put("product_id", productId);
            
            if ("funds".equals(quantityType)) {
                orderRequest.put("funds", quantity);
            } else if ("size".equals(quantityType)) {
                orderRequest.put("size", quantity);
            }
            
            String body = mapper.writeValueAsString(orderRequest);
            String signature = generateSignature(timestamp, method, requestPath, body);
            
            System.out.println("Request body: " + body);
            
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
            
            System.out.println("✓ Success! Response: " + response);
            
        } catch (WebClientResponseException e) {
            System.err.println("✗ Failed with status: " + e.getStatusCode());
            System.err.println("Error response: " + e.getResponseBodyAsString());
            
            // Try to parse error response
            try {
                JsonNode error = mapper.readTree(e.getResponseBodyAsString());
                if (error.has("message")) {
                    System.err.println("Error message: " + error.get("message").asText());
                }
            } catch (Exception parseEx) {
                // Ignore parse errors
            }
        } catch (Exception e) {
            System.err.println("✗ Failed with error: " + e.getMessage());
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