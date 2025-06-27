package com.example.sparechange.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDto {
    private String id;
    private String productId;
    private String side;
    private String type;
    private String status;
    private BigDecimal size;
    private BigDecimal price;
    private BigDecimal funds;
    private BigDecimal filledSize;
    private BigDecimal executedValue;
    private BigDecimal fillFees;
    private LocalDateTime createdAt;
    private LocalDateTime doneAt;
    private String doneReason;
    private boolean settled;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getSize() { return size; }
    public void setSize(BigDecimal size) { this.size = size; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getFunds() { return funds; }
    public void setFunds(BigDecimal funds) { this.funds = funds; }
    
    public BigDecimal getFilledSize() { return filledSize; }
    public void setFilledSize(BigDecimal filledSize) { this.filledSize = filledSize; }
    
    public BigDecimal getExecutedValue() { return executedValue; }
    public void setExecutedValue(BigDecimal executedValue) { this.executedValue = executedValue; }
    
    public BigDecimal getFillFees() { return fillFees; }
    public void setFillFees(BigDecimal fillFees) { this.fillFees = fillFees; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getDoneAt() { return doneAt; }
    public void setDoneAt(LocalDateTime doneAt) { this.doneAt = doneAt; }
    
    public String getDoneReason() { return doneReason; }
    public void setDoneReason(String doneReason) { this.doneReason = doneReason; }
    
    public boolean isSettled() { return settled; }
    public void setSettled(boolean settled) { this.settled = settled; }
}