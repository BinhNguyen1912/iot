package com.nguyenanhbinh.lab306new.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyenanhbinh.lab306new.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
