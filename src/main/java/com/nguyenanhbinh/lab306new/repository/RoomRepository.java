package com.nguyenanhbinh.lab306new.repository;

import com.nguyenanhbinh.lab306new.model.Room;
import com.nguyenanhbinh.lab306new.model.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findBySpace(Space space);
}
