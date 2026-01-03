package com.nguyenanhbinh.lab306new.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nguyenanhbinh.lab306new.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your OTP Verification Code");
            helper.setText(buildEmailTemplate(otp), true);

            mailSender.send(message);
            log.info("✅ OTP email sent to {}", toEmail);

        } catch (Exception e) {
            // ❗ TUYỆT ĐỐI KHÔNG THROW
            log.error("❌ Failed to send OTP email to {}", toEmail, e);
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
