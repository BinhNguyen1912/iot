package com.nguyenanhbinh.lab306new.service;

import com.nguyenanhbinh.lab306new.model.User;

public interface TokenService {

    String createToken(User user);

    void deleteToken(String token);

    boolean isValid(String token);
}
