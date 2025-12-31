package com.nguyenanhbinh.lab306new.service.impl;

import com.nguyenanhbinh.lab306new.model.User;
import com.nguyenanhbinh.lab306new.repository.UserRepository;
import com.nguyenanhbinh.lab306new.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findOrCreateByEmail(String email) {
        return userRepository.findByUsername(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername(email);
                    user.setPassword("");
                    return userRepository.save(user);
                });
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {

        Optional<User> optionalUser = userRepository.findByUsername(email);

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        if (user.getOtp() == null) {
            return false;
        }

        // So sánh OTP
        if (user.getOtp().equals(otp)) {
            // ✅ Đúng → xóa OTP sau khi verify
            user.setOtp(null);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    @Override
    public boolean changePassword(String email, String newPassword) {

        return userRepository.findByUsername(email)
                .map(user -> {
                    user.setPassword(newPassword);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
}
