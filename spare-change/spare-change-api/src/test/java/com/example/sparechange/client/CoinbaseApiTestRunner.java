package com.example.sparechange.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.example.sparechange")
public class CoinbaseApiTestRunner {
    
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CoinbaseApiTestRunner.class, args);
        
        try {
            CoinbaseClient client = context.getBean(CoinbaseClient.class);
            
            System.out.println("\n=== Testing Coinbase Sandbox API ===\n");
            
            // Test 1: List orders
            System.out.println("Test 1: Listing orders...");
            try {
                var orders = client.listOrders();
                System.out.println("✓ Successfully retrieved " + orders.size() + " orders");
            } catch (Exception e) {
                System.err.println("✗ Failed to list orders: " + e.getMessage());
            }
            
            System.out.println("\nPress Ctrl+C to exit.");
            Thread.sleep(5000);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}