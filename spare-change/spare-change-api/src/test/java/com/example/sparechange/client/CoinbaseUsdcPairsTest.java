package com.example.sparechange.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
public class CoinbaseUsdcPairsTest {
    
    @Test
    public void findUsdcPairs() throws Exception {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api-public.sandbox.exchange.coinbase.com")
                .build();
        
        ObjectMapper mapper = new ObjectMapper();
        
        System.out.println("=== Finding All USDC Trading Pairs ===");
        
        String productsJson = webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        
        JsonNode products = mapper.readTree(productsJson);
        
        System.out.println("\nUSC and USD related pairs:");
        for (JsonNode product : products) {
            String id = product.get("id").asText();
            String baseCurrency = product.get("base_currency").asText();
            String quoteCurrency = product.get("quote_currency").asText();
            String status = product.has("status") ? product.get("status").asText() : "unknown";
            boolean tradingDisabled = product.has("trading_disabled") ? product.get("trading_disabled").asBoolean() : true;
            
            if (id.contains("USDC") || id.contains("USD")) {
                System.out.println(String.format("%-15s: base=%-5s quote=%-5s status=%-10s trading_disabled=%s", 
                    id, baseCurrency, quoteCurrency, status, tradingDisabled));
                
                if (baseCurrency.equals("BTC") && quoteCurrency.equals("USDC")) {
                    System.out.println("  -> Can buy BTC with USDC using this pair!");
                }
            }
        }
        
        System.out.println("\n=== Solution ===");
        System.out.println("Since we have USDC and want to buy BTC:");
        System.out.println("1. We should use LINK-USDC or BAT-USDC pairs if they're online");
        System.out.println("2. OR we need to deposit USD to the sandbox account");
        System.out.println("3. OR we can try buying on a USDC quote pair directly");
    }
}