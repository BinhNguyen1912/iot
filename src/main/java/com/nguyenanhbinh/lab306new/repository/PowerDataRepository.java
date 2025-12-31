package com.nguyenanhbinh.lab306new.repository;

import com.nguyenanhbinh.lab306new.model.PowerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PowerDataRepository extends JpaRepository<PowerData, Long> {

    // Lấy dữ liệu mới nhất
    @Query("SELECT p FROM PowerData p ORDER BY p.timestamp DESC")
    Optional<PowerData> findLatest();
}