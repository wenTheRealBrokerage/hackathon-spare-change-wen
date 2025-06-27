package com.example.sparechange.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "roundup_summaries")
public class RoundUpSummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal totalUsd;
    
    private LocalDateTime createdAt;
    
    private String coinbaseOrderId;
    
    @Column(name = "product_id", length = 20)
    private String productId;
    
    public RoundUpSummary() {}
    
    public RoundUpSummary(Long id, BigDecimal totalUsd, LocalDateTime createdAt, String coinbaseOrderId) {
        this.id = id;
        this.totalUsd = totalUsd;
        this.createdAt = createdAt;
        this.coinbaseOrderId = coinbaseOrderId;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getTotalUsd() {
        return totalUsd;
    }
    
    public void setTotalUsd(BigDecimal totalUsd) {
        this.totalUsd = totalUsd;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getCoinbaseOrderId() {
        return coinbaseOrderId;
    }
    
    public void setCoinbaseOrderId(String coinbaseOrderId) {
        this.coinbaseOrderId = coinbaseOrderId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
}