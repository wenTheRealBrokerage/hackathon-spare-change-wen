package com.example.sparechange.controller;

import com.example.sparechange.client.ICoinbaseClient;
import com.example.sparechange.client.OrderDto;
import com.example.sparechange.entity.RoundUpSummary;
import com.example.sparechange.service.ThresholdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roundup")
public class RoundUpController {
    
    private final ThresholdService thresholdService;
    private final ICoinbaseClient coinbaseClient;
    
    public RoundUpController(ThresholdService thresholdService, ICoinbaseClient coinbaseClient) {
        this.thresholdService = thresholdService;
        this.coinbaseClient = coinbaseClient;
    }
    
    @GetMapping("/orders")
    public ResponseEntity<Page<RoundUpSummary>> getRoundUpOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RoundUpSummary> orders = thresholdService.getRoundUpSummaries(pageable);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/orders/all")
    public ResponseEntity<List<RoundUpSummary>> getAllRoundUpOrders() {
        List<RoundUpSummary> orders = thresholdService.getAllRoundUpSummaries();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getRoundUpSummary() {
        List<RoundUpSummary> allOrders = thresholdService.getAllRoundUpSummaries();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalOrders", allOrders.size());
        summary.put("totalUsdConverted", allOrders.stream()
                .map(RoundUpSummary::getTotalUsd)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        summary.put("orders", allOrders);
        
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/coinbase/orders")
    public ResponseEntity<List<OrderDto>> getCoinbaseOrders() {
        try {
            List<OrderDto> allOrders = coinbaseClient.listOrders();
            return ResponseEntity.ok(allOrders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/coinbase/orders/btc")
    public ResponseEntity<List<OrderDto>> getCoinbaseBtcOrders() {
        try {
            List<OrderDto> allOrders = coinbaseClient.listOrders();
            // Filter for BTC-USD orders only
            List<OrderDto> btcOrders = allOrders.stream()
                    .filter(order -> "BTC-USD".equals(order.getProductId()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(btcOrders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/coinbase/orders/summary")
    public ResponseEntity<Map<String, Object>> getCoinbaseOrdersSummary() {
        try {
            List<OrderDto> allOrders = coinbaseClient.listOrders();
            
            // Get our round-up summaries to cross-reference
            List<RoundUpSummary> roundUpSummaries = thresholdService.getAllRoundUpSummaries();
            List<String> ourOrderIds = roundUpSummaries.stream()
                    .map(RoundUpSummary::getCoinbaseOrderId)
                    .collect(Collectors.toList());
            
            // Filter Coinbase orders to only those created by our app
            List<OrderDto> ourCoinbaseOrders = allOrders.stream()
                    .filter(order -> ourOrderIds.contains(order.getId()))
                    .collect(Collectors.toList());
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalCoinbaseOrders", allOrders.size());
            summary.put("ourRoundUpOrders", ourCoinbaseOrders.size());
            summary.put("allOrders", allOrders);
            summary.put("ourOrders", ourCoinbaseOrders);
            
            // Group by status
            Map<String, List<OrderDto>> ordersByStatus = ourCoinbaseOrders.stream()
                    .collect(Collectors.groupingBy(OrderDto::getStatus));
            summary.put("ordersByStatus", ordersByStatus);
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/coinbase/orders/{orderId}")
    public ResponseEntity<Map<String, Object>> getCoinbaseOrderDetails(@PathVariable String orderId) {
        try {
            List<OrderDto> allOrders = coinbaseClient.listOrders();
            
            // Find specific order
            OrderDto order = allOrders.stream()
                    .filter(o -> orderId.equals(o.getId()))
                    .findFirst()
                    .orElse(null);
            
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Get our local round-up summary if it exists
            RoundUpSummary localSummary = thresholdService.getAllRoundUpSummaries().stream()
                    .filter(s -> orderId.equals(s.getCoinbaseOrderId()))
                    .findFirst()
                    .orElse(null);
            
            Map<String, Object> details = new HashMap<>();
            details.put("coinbaseOrder", order);
            details.put("localSummary", localSummary);
            
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}