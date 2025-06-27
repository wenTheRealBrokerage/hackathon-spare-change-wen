package com.example.sparechange.service;

import com.example.sparechange.entity.Tx;
import com.example.sparechange.entity.TxStatus;
import com.example.sparechange.repository.TxRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class TxService {
    
    private final TxRepository txRepository;
    private final Sinks.Many<Tx> txSink = Sinks.many().multicast().onBackpressureBuffer();
    
    public TxService(TxRepository txRepository) {
        this.txRepository = txRepository;
    }
    
    public Tx ingestTx(Tx tx) {
        tx.setTs(LocalDateTime.now());
        tx.setStatus(TxStatus.NEW);
        
        BigDecimal roundedUp = tx.getAmountUsd().setScale(0, RoundingMode.UP);
        BigDecimal spare = roundedUp.subtract(tx.getAmountUsd());
        tx.setSpareUsd(spare);
        
        Tx savedTx = txRepository.save(tx);
        txSink.tryEmitNext(savedTx);
        
        return savedTx;
    }
    
    public Page<Tx> getTxList(Pageable pageable) {
        return txRepository.findAll(pageable);
    }
    
    public Flux<Tx> streamTx() {
        return txSink.asFlux();
    }
    
    public BigDecimal getTotalSpareChange() {
        return txRepository.sumSpareUsdByStatus(TxStatus.NEW);
    }
}