package com.nguyenanhbinh.lab306new.service;

import com.nguyenanhbinh.lab306new.model.User;

public interface UserService {

    User findOrCreateByEmail(String email);

    void save(User user);

    boolean verifyOtp(String email, String otp);

    boolean changePassword(String email, String newPassword);
}
