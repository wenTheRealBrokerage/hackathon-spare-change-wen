package com.example.sparechange.controller;

import com.example.sparechange.entity.Tx;
import com.example.sparechange.service.TxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/tx")
public class TxController {
    
    private final TxService txService;
    
    public TxController(TxService txService) {
        this.txService = txService;
    }
    
    @PostMapping
    public Tx ingestTx(@RequestBody Tx tx) {
        return txService.ingestTx(tx);
    }
    
    @GetMapping
    public Page<Tx> getTxList(Pageable pageable) {
        return txService.getTxList(pageable);
    }
    
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Tx> streamTx() {
        return txService.streamTx()
                .delayElements(Duration.ofSeconds(1));
    }
}