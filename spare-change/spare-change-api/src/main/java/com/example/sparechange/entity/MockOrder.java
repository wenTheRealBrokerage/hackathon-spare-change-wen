package com.example.sparechange.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mock_orders")
public class MockOrder {
    
    @Id
    private String id;
    
    @Column(name = "product_id", nullable = false)
    private String productId;
    
    @Column(nullable = false)
    private String side;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private String status;
    
    @Column(nullable = false)
    private boolean settled;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "filled_size", precision = 19, scale = 8)
    private BigDecimal filledSize;
    
    @Column(name = "executed_value", precision = 19, scale = 2)
    private BigDecimal executedValue;
    
    @Column(name = "fill_fees", precision = 19, scale = 2)
    private BigDecimal fillFees;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getSide() {
        return side;
    }
    
    public void setSide(String side) {
        this.side = side;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isSettled() {
        return settled;
    }
    
    public void setSettled(boolean settled) {
        this.settled = settled;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public BigDecimal getFilledSize() {
        return filledSize;
    }
    
    public void setFilledSize(BigDecimal filledSize) {
        this.filledSize = filledSize;
    }
    
    public BigDecimal getExecutedValue() {
        return executedValue;
    }
    
    public void setExecutedValue(BigDecimal executedValue) {
        this.executedValue = executedValue;
    }
    
    public BigDecimal getFillFees() {
        return fillFees;
    }
    
    public void setFillFees(BigDecimal fillFees) {
        this.fillFees = fillFees;
    }
}