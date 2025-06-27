package com.example.sparechange.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Tx {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String merchant;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal amountUsd;
    
    private LocalDateTime ts;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal spareUsd;
    
    @Enumerated(EnumType.STRING)
    private TxStatus status = TxStatus.NEW;
    
    private String coinbaseOrderId;
    
    public Tx() {}
    
    public Tx(Long id, String merchant, BigDecimal amountUsd, LocalDateTime ts, 
              BigDecimal spareUsd, TxStatus status, String coinbaseOrderId) {
        this.id = id;
        this.merchant = merchant;
        this.amountUsd = amountUsd;
        this.ts = ts;
        this.spareUsd = spareUsd;
        this.status = status;
        this.coinbaseOrderId = coinbaseOrderId;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMerchant() {
        return merchant;
    }
    
    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }
    
    public BigDecimal getAmountUsd() {
        return amountUsd;
    }
    
    public void setAmountUsd(BigDecimal amountUsd) {
        this.amountUsd = amountUsd;
    }
    
    public LocalDateTime getTs() {
        return ts;
    }
    
    public void setTs(LocalDateTime ts) {
        this.ts = ts;
    }
    
    public BigDecimal getSpareUsd() {
        return spareUsd;
    }
    
    public void setSpareUsd(BigDecimal spareUsd) {
        this.spareUsd = spareUsd;
    }
    
    public TxStatus getStatus() {
        return status;
    }
    
    public void setStatus(TxStatus status) {
        this.status = status;
    }
    
    public String getCoinbaseOrderId() {
        return coinbaseOrderId;
    }
    
    public void setCoinbaseOrderId(String coinbaseOrderId) {
        this.coinbaseOrderId = coinbaseOrderId;
    }
}