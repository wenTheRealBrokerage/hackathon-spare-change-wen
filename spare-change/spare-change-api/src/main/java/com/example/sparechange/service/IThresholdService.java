package com.example.sparechange.service;

import com.example.sparechange.entity.RoundUpSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IThresholdService {
    boolean checkAndExecute();
    Page<RoundUpSummary> getRoundUpSummaries(Pageable pageable);
    List<RoundUpSummary> getAllRoundUpSummaries();
}