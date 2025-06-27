package com.example.sparechange.controller;

import com.example.sparechange.entity.MockOrder;
import com.example.sparechange.repository.MockOrderRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mock-orders")
public class MockOrderController {
    
    private final MockOrderRepository mockOrderRepository;
    
    public MockOrderController(MockOrderRepository mockOrderRepository) {
        this.mockOrderRepository = mockOrderRepository;
    }
    
    @GetMapping
    public List<MockOrder> getAllMockOrders() {
        return mockOrderRepository.findAllByOrderByCreatedAtDesc();
    }
}