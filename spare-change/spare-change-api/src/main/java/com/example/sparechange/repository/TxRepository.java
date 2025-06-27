package com.example.sparechange.repository;

import com.example.sparechange.entity.Tx;
import com.example.sparechange.entity.TxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

@Repository
public interface TxRepository extends JpaRepository<Tx, Long> {
    
    @Query("SELECT COALESCE(SUM(t.spareUsd), 0) FROM Tx t WHERE t.status = :status")
    BigDecimal sumSpareUsdByStatus(@Param("status") TxStatus status);
    
    Page<Tx> findByStatus(TxStatus status, Pageable pageable);
    
    @Query("SELECT t FROM Tx t ORDER BY CASE WHEN t.status = 'NEW' THEN 0 ELSE 1 END, t.ts DESC")
    Page<Tx> findAllOrderedByStatusAndDate(Pageable pageable);
}