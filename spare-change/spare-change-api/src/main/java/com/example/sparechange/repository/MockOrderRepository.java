package com.example.sparechange.repository;

import com.example.sparechange.entity.MockOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockOrderRepository extends JpaRepository<MockOrder, String> {
    List<MockOrder> findAllByOrderByCreatedAtDesc();
}