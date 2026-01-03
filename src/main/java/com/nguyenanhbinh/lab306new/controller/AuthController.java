package com.nguyenanhbinh.lab306new.controller;

import com.nguyenanhbinh.lab306new.model.User;
import com.nguyenanhbinh.lab306new.service.AuthService;
import com.nguyenanhbinh.lab306new.service.EmailService;
import com.nguyenanhbinh.lab306new.service.UserService;
import com.nguyenanhbinh.lab306new.util.OtpUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final UserService userService;

    public AuthController(AuthService authService,
            EmailService emailService, UserService userService) {
        this.authService = authService;
        this.emailService = emailService;
        this.userService = userService;
    }

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        Map<String, Object> result = authService.register(username, password);

        return ResponseEntity.ok(result);
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        Map<String, Object> result = authService.login(username, password);

        return ResponseEntity.ok(result);
    }

    // =========================
    // SEND OTP (4 DIGITS)
    // =========================
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Email is required"));
        }

        User user = userService.findOrCreateByEmail(email);
        String otp = OtpUtil.generateOtp4Digits();

        user.setOtp(otp);
        userService.save(user);

        // ✅ Gửi async, không block
        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(
                Map.of("success", true, "message", "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || otp == null || email.isBlank() || otp.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Email and OTP are required"));
        }

        boolean result = userService.verifyOtp(email, otp);

        if (result) {
            return ResponseEntity.ok(
                    Map.of("success", true));
        } else {
            return ResponseEntity.ok(
                    Map.of("success", false));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");

        if (email == null || newPassword == null || confirmPassword == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Missing fields"));
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.ok(
                    Map.of("success", false, "message", "Passwords do not match"));
        }

        boolean result = userService.changePassword(email, newPassword);

        if (result) {
            return ResponseEntity.ok(
                    Map.of("success", true));
        } else {
            return ResponseEntity.ok(
                    Map.of("success", false, "message", "User not found"));
        }
    }

}
