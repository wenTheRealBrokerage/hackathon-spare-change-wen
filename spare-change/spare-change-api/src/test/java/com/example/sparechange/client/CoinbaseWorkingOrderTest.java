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
public class CoinbaseWorkingOrderTest {
    
    private final String apiKey = "338d76f0ed9ebde87d0c5c847aa21508";
    private final String apiSecret = "Iw5f0WlsW1eVFqxGYXhBg+J4WsG651Rgj25xbhSxgW7GLI/8FhA3vYpnzhLR1zASEbyGdDtVWTjtjVKYkiLzaQ==";
    private final String passphrase = "66hvzqqxoqi9";
    
    @Test
    public void testMultiStepConversion() throws Exception {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api-public.sandbox.exchange.coinbase.com")
                .build();
        
        ObjectMapper mapper = new ObjectMapper();
        
        System.out.println("=== Multi-Step USDC to BTC Conversion ===");
        System.out.println("Since we can't directly convert USDC to BTC, we'll use LINK as an intermediary");
        
        // Initial balance check
        System.out.println("\n1. Initial Balances:");
        checkBalances(webClient, mapper);
        
        // Step 1: Buy LINK with USDC
        System.out.println("\n2. Buying LINK with USDC:");
        String linkOrderId = placeOrder(webClient, mapper, "LINK-USDC", "buy", "market", "funds", "50.00");
        if (linkOrderId != null) {
            Thread.sleep(2000);
            checkBalances(webClient, mapper);
        }
        
        // Step 2: Sell LINK for USD
        System.out.println("\n3. Selling LINK for USD:");
        String usdOrderId = placeOrder(webClient, mapper, "LINK-USD", "sell", "market", "size", "all");
        if (usdOrderId != null) {
            Thread.sleep(2000);
            checkBalances(webClient, mapper);
        }
        
        // Step 3: Buy BTC with USD
        System.out.println("\n4. Buying BTC with USD:");
        String btcOrderId = placeOrder(webClient, mapper, "BTC-USD", "buy", "market", "funds", "10.00");
        if (btcOrderId != null) {
            Thread.sleep(2000);
            checkBalances(webClient, mapper);
        }
        
        System.out.println("\n=== Summary ===");
        System.out.println("For the actual implementation, we should:");
        System.out.println("1. Keep it simple - just try to buy BTC-USD directly");
        System.out.println("2. Assume the account has been funded with USD");
        System.out.println("3. Or implement a proper conversion mechanism if needed");
    }
    
    private String placeOrder(WebClient webClient, ObjectMapper mapper, String productId, 
                             String side, String type, String quantityType, String quantity) {
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
                if ("all".equals(quantity)) {
                    // Get available balance first
                    String currency = productId.split("-")[0];
                    String balance = getAvailableBalance(webClient, mapper, currency);
                    if (balance != null && Double.parseDouble(balance) > 0) {
                        orderRequest.put("size", balance);
                    } else {
                        System.out.println("No " + currency + " balance available");
                        return null;
                    }
                } else {
                    orderRequest.put("size", quantity);
                }
            }
            
            String body = mapper.writeValueAsString(orderRequest);
            String signature = generateSignature(timestamp, method, requestPath, body);
            
            System.out.println("Placing order: " + body);
            
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
            
            JsonNode orderResponse = mapper.readTree(response);
            String orderId = orderResponse.get("id").asText();
            System.out.println("✓ Order placed successfully! Order ID: " + orderId);
            return orderId;
            
        } catch (WebClientResponseException e) {
            System.err.println("✗ Failed to place order: " + e.getStatusCode());
            System.err.println("Error: " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            return null;
        }
    }
    
    private String getAvailableBalance(WebClient webClient, ObjectMapper mapper, String currency) {
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
                if (account.get("currency").asText().equals(currency)) {
                    return account.get("available").asText();
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting balance: " + e.getMessage());
        }
        return null;
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
                
                if (availableNum > 0 || currency.equals("USD") || currency.equals("USDC") || 
                    currency.equals("BTC") || currency.equals("LINK")) {
                    System.out.println("  " + currency + ": " + available);
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