package com.nguyenanhbinh.lab306new.repository;

import com.nguyenanhbinh.lab306new.model.Room;
import com.nguyenanhbinh.lab306new.model.SmartDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SmartDeviceRepository extends JpaRepository<SmartDevice, Long> {

    List<SmartDevice> findByRoom(Room room);

    Optional<SmartDevice> findByRoomAndName(Room room, String name);
}
