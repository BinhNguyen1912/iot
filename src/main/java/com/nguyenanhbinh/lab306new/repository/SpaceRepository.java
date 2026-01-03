package com.nguyenanhbinh.lab306new.repository;

import com.nguyenanhbinh.lab306new.model.Space;
import com.nguyenanhbinh.lab306new.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceRepository extends JpaRepository<Space, Long> {

    List<Space> findByOwner(User owner);
}
