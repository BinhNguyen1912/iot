package com.nguyenanhbinh.lab306new.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.nguyenanhbinh.lab306new.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your OTP Verification Code");
            helper.setText(buildEmailTemplate(otp), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email");
        }
    }

    private String buildEmailTemplate(String otp) {
        return """
                <html>
                  <body style="font-family: Arial, sans-serif;">
                    <h2>Your OTP Code</h2>
                    <p>Use the following OTP to verify your email:</p>
                    <h1 style="color:#4A6CF7;">%s</h1>
                    <p>This OTP will expire in 5 minutes.</p>
                  </body>
                </html>
                """.formatted(otp);
    }
}
