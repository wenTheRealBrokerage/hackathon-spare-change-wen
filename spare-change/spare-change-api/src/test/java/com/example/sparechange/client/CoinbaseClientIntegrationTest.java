package com.example.sparechange.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("default")
public class CoinbaseClientIntegrationTest {
    
    @Autowired
    private CoinbaseClient coinbaseClient;
    
    @Test
    public void testListOrders() {
        System.out.println("Testing Coinbase API connection...");
        
        try {
            List<OrderDto> orders = coinbaseClient.listOrders();
            System.out.println("Successfully retrieved " + orders.size() + " orders");
            
            for (OrderDto order : orders) {
                System.out.println("Order ID: " + order.getId() + 
                                 ", Status: " + order.getStatus() + 
                                 ", Product: " + order.getProductId());
            }
            
            assertNotNull(orders);
        } catch (Exception e) {
            System.err.println("Error calling Coinbase API: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Test
    public void testBuyUsdcToBtc_SmallAmount() {
        System.out.println("Testing small BTC buy order...");
        
        try {
            // Try with minimum amount
            BigDecimal amount = new BigDecimal("1.00");
            String orderId = coinbaseClient.buyUsdcToBtc(amount);
            
            System.out.println("Successfully created order with ID: " + orderId);
            assertNotNull(orderId);
            assertFalse(orderId.isEmpty());
            
        } catch (Exception e) {
            System.err.println("Error creating buy order: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}