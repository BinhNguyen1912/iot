package com.nguyenanhbinh.lab306new.repository;

import com.nguyenanhbinh.lab306new.model.PowerData;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PowerDataRepository extends JpaRepository<PowerData, Long> {

    // Lấy dữ liệu mới nhất
    @Query("SELECT p FROM PowerData p ORDER BY p.timestamp DESC")
    Optional<PowerData> findLatest();

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE power_data RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncatePowerData();
}