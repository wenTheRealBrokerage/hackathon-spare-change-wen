package com.example.sparechange.repository;

import com.example.sparechange.entity.RoundUpSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundUpSummaryRepository extends JpaRepository<RoundUpSummary, Long> {
}