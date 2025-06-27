package com.example.sparechange.controller;

import com.example.sparechange.entity.RoundUpSummary;
import com.example.sparechange.repository.RoundUpSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RoundUpControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private RoundUpSummaryRepository roundUpSummaryRepository;
    
    @BeforeEach
    void setUp() {
        roundUpSummaryRepository.deleteAll();
    }
    
    @Test
    public void testGetRoundUpOrders() throws Exception {
        // Create test data
        RoundUpSummary order1 = new RoundUpSummary();
        order1.setTotalUsd(new BigDecimal("5.50"));
        order1.setCreatedAt(LocalDateTime.now().minusHours(2));
        order1.setCoinbaseOrderId("order-123");
        roundUpSummaryRepository.save(order1);
        
        RoundUpSummary order2 = new RoundUpSummary();
        order2.setTotalUsd(new BigDecimal("7.25"));
        order2.setCreatedAt(LocalDateTime.now().minusHours(1));
        order2.setCoinbaseOrderId("order-456");
        roundUpSummaryRepository.save(order2);
        
        // Test paginated endpoint
        mockMvc.perform(get("/roundup/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].coinbaseOrderId", is("order-456"))) // Most recent first
                .andExpect(jsonPath("$.content[0].totalUsd", is(7.25)))
                .andExpect(jsonPath("$.content[1].coinbaseOrderId", is("order-123")))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }
    
    @Test
    public void testGetAllRoundUpOrders() throws Exception {
        // Create test data
        RoundUpSummary order1 = new RoundUpSummary();
        order1.setTotalUsd(new BigDecimal("5.50"));
        order1.setCreatedAt(LocalDateTime.now());
        order1.setCoinbaseOrderId("order-789");
        roundUpSummaryRepository.save(order1);
        
        // Test all orders endpoint
        mockMvc.perform(get("/roundup/orders/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].coinbaseOrderId", is("order-789")))
                .andExpect(jsonPath("$[0].totalUsd", is(5.50)));
    }
    
    @Test
    public void testGetRoundUpSummary() throws Exception {
        // Create test data
        RoundUpSummary order1 = new RoundUpSummary();
        order1.setTotalUsd(new BigDecimal("5.50"));
        order1.setCreatedAt(LocalDateTime.now());
        order1.setCoinbaseOrderId("order-001");
        roundUpSummaryRepository.save(order1);
        
        RoundUpSummary order2 = new RoundUpSummary();
        order2.setTotalUsd(new BigDecimal("8.75"));
        order2.setCreatedAt(LocalDateTime.now());
        order2.setCoinbaseOrderId("order-002");
        roundUpSummaryRepository.save(order2);
        
        // Test summary endpoint
        mockMvc.perform(get("/roundup/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", is(2)))
                .andExpect(jsonPath("$.totalUsdConverted", is(14.25))) // 5.50 + 8.75
                .andExpect(jsonPath("$.orders", hasSize(2)));
    }
    
    @Test
    public void testEmptyRoundUpOrders() throws Exception {
        // Test with no orders
        mockMvc.perform(get("/roundup/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
        
        mockMvc.perform(get("/roundup/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", is(0)))
                .andExpect(jsonPath("$.totalUsdConverted", is(0)));
    }
}