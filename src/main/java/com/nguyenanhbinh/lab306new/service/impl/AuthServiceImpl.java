package com.nguyenanhbinh.lab306new.service.impl;

import org.springframework.stereotype.Service;

import com.nguyenanhbinh.lab306new.model.User;
import com.nguyenanhbinh.lab306new.repository.UserRepository;
import com.nguyenanhbinh.lab306new.security.JwtUtil;
import com.nguyenanhbinh.lab306new.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Map<String, Object> login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());

        response.put("user", userInfo);

        return response;
    }

    @Override
    public Map<String, Object> register(String username, String password) {

        // 1. Validate
        if (username == null || password == null ||
                username.isBlank() || password.isBlank()) {
            throw new RuntimeException("Username and password are required");
        }

        // 2. Check tồn tại
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        // 3. Lưu user
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password.trim()); // chưa hash theo yêu cầu của bạn

        userRepository.save(user);

        // 4. Sinh JWT (GIỐNG LOGIN)
        String token = jwtUtil.generateToken(user.getUsername());

        // 5. Response giống login
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Register successful");
        response.put("accessToken", token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());

        response.put("user", userInfo);

        return response;
    }

}
