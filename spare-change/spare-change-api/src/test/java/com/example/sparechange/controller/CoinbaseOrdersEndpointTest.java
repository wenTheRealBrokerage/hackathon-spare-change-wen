package com.example.sparechange.controller;

import com.example.sparechange.client.ICoinbaseClient;
import com.example.sparechange.client.OrderDto;
import com.example.sparechange.entity.RoundUpSummary;
import com.example.sparechange.repository.RoundUpSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CoinbaseOrdersEndpointTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ICoinbaseClient coinbaseClient;
    
    @Autowired
    private RoundUpSummaryRepository roundUpSummaryRepository;
    
    @BeforeEach
    void setUp() {
        roundUpSummaryRepository.deleteAll();
    }
    
    @Test
    public void testGetAllCoinbaseOrders() throws Exception {
        // Mock Coinbase API response
        OrderDto order1 = createMockOrder("order-001", "BTC-USD", "buy", "done", "5.00", "0.0001");
        OrderDto order2 = createMockOrder("order-002", "ETH-USD", "buy", "pending", "10.00", "0.005");
        OrderDto order3 = createMockOrder("order-003", "BTC-USD", "sell", "done", "7.50", "0.00015");
        
        when(coinbaseClient.listOrders()).thenReturn(Arrays.asList(order1, order2, order3));
        
        mockMvc.perform(get("/roundup/coinbase/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is("order-001")))
                .andExpect(jsonPath("$[1].id", is("order-002")))
                .andExpect(jsonPath("$[2].id", is("order-003")));
    }
    
    @Test
    public void testGetBtcOrdersOnly() throws Exception {
        // Mock Coinbase API response with mixed products
        OrderDto btcOrder1 = createMockOrder("order-001", "BTC-USD", "buy", "done", "5.00", "0.0001");
        OrderDto ethOrder = createMockOrder("order-002", "ETH-USD", "buy", "done", "10.00", "0.005");
        OrderDto btcOrder2 = createMockOrder("order-003", "BTC-USD", "buy", "done", "7.50", "0.00015");
        
        when(coinbaseClient.listOrders()).thenReturn(Arrays.asList(btcOrder1, ethOrder, btcOrder2));
        
        mockMvc.perform(get("/roundup/coinbase/orders/btc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productId", is("BTC-USD")))
                .andExpect(jsonPath("$[1].productId", is("BTC-USD")));
    }
    
    @Test
    public void testGetCoinbaseOrdersSummary() throws Exception {
        // Create local round-up summary
        RoundUpSummary localSummary = new RoundUpSummary();
        localSummary.setTotalUsd(new BigDecimal("5.00"));
        localSummary.setCreatedAt(LocalDateTime.now());
        localSummary.setCoinbaseOrderId("order-001");
        roundUpSummaryRepository.save(localSummary);
        
        // Mock Coinbase API response
        OrderDto ourOrder = createMockOrder("order-001", "BTC-USD", "buy", "done", "5.00", "0.0001");
        OrderDto otherOrder = createMockOrder("order-999", "BTC-USD", "buy", "done", "100.00", "0.002");
        
        when(coinbaseClient.listOrders()).thenReturn(Arrays.asList(ourOrder, otherOrder));
        
        mockMvc.perform(get("/roundup/coinbase/orders/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCoinbaseOrders", is(2)))
                .andExpect(jsonPath("$.ourRoundUpOrders", is(1)))
                .andExpect(jsonPath("$.ourOrders", hasSize(1)))
                .andExpect(jsonPath("$.ourOrders[0].id", is("order-001")))
                .andExpect(jsonPath("$.ordersByStatus.done", hasSize(1)));
    }
    
    @Test
    public void testGetSpecificOrderDetails() throws Exception {
        // Create local round-up summary
        RoundUpSummary localSummary = new RoundUpSummary();
        localSummary.setTotalUsd(new BigDecimal("5.00"));
        localSummary.setCreatedAt(LocalDateTime.now());
        localSummary.setCoinbaseOrderId("order-001");
        roundUpSummaryRepository.save(localSummary);
        
        // Mock Coinbase API response
        OrderDto order = createMockOrder("order-001", "BTC-USD", "buy", "done", "5.00", "0.0001");
        when(coinbaseClient.listOrders()).thenReturn(Arrays.asList(order));
        
        mockMvc.perform(get("/roundup/coinbase/orders/order-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coinbaseOrder.id", is("order-001")))
                .andExpect(jsonPath("$.coinbaseOrder.status", is("done")))
                .andExpect(jsonPath("$.localSummary.coinbaseOrderId", is("order-001")))
                .andExpect(jsonPath("$.localSummary.totalUsd", is(5.0)));
    }
    
    @Test
    public void testOrderNotFound() throws Exception {
        when(coinbaseClient.listOrders()).thenReturn(Arrays.asList());
        
        mockMvc.perform(get("/roundup/coinbase/orders/non-existent"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testCoinbaseApiError() throws Exception {
        when(coinbaseClient.listOrders()).thenThrow(new RuntimeException("Coinbase API error"));
        
        mockMvc.perform(get("/roundup/coinbase/orders"))
                .andExpect(status().isInternalServerError());
        
        mockMvc.perform(get("/roundup/coinbase/orders/summary"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Coinbase API error")));
    }
    
    private OrderDto createMockOrder(String id, String productId, String side, String status, 
                                    String funds, String size) {
        OrderDto order = new OrderDto();
        order.setId(id);
        order.setProductId(productId);
        order.setSide(side);
        order.setStatus(status);
        order.setSettled(true);
        order.setCreatedAt(LocalDateTime.now());
        order.setType("market");
        order.setFilledSize(new BigDecimal(size));
        order.setExecutedValue(new BigDecimal(funds));
        order.setFillFees(new BigDecimal("0.05"));
        return order;
    }
}