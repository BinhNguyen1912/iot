package com.nguyenanhbinh.lab306new.repository;

import com.nguyenanhbinh.lab306new.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    java.util.List<Device> findByTopic(String topic);
}