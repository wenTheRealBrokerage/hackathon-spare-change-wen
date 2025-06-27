package com.example.sparechange.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
public class CoinbaseProductTest {
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Test
    public void listAvailableProducts() {
        WebClient webClient = webClientBuilder
                .baseUrl("https://api-public.sandbox.exchange.coinbase.com")
                .build();
        
        System.out.println("=== Available Products in Sandbox ===");
        
        try {
            String products = webClient.get()
                    .uri("/products")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            System.out.println("Products: " + products);
            
            // Also try to get specific product info
            System.out.println("\n=== Checking BTC-USD ===");
            try {
                String btcUsd = webClient.get()
                        .uri("/products/BTC-USD")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                System.out.println("BTC-USD: " + btcUsd);
            } catch (Exception e) {
                System.err.println("BTC-USD not available: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}