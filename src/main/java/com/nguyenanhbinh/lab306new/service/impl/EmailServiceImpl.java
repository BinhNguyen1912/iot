// package com.nguyenanhbinh.lab306new.service.impl;

// import jakarta.mail.internet.MimeMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;

// import com.nguyenanhbinh.lab306new.service.EmailService;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// @Service
// public class EmailServiceImpl implements EmailService {

//     private final JavaMailSender mailSender;
//     private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

//     public EmailServiceImpl(JavaMailSender mailSender) {
//         this.mailSender = mailSender;
//     }

//     @Override
//     @Async
//     public void sendOtpEmail(String toEmail, String otp) {
//         try {
//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

//             helper.setTo(toEmail);
//             helper.setSubject("Your OTP Verification Code");
//             helper.setText(buildEmailTemplate(otp), true);

//             mailSender.send(message);
//             log.info("✅ OTP email sent to {}", toEmail);

//         } catch (Exception e) {
//             // ❗ TUYỆT ĐỐI KHÔNG THROW
//             log.error("❌ Failed to send OTP email to {}", toEmail, e);
//         }
//     }

//     private String buildEmailTemplate(String otp) {
//         return """
//                 <html>
//                   <body style="font-family: Arial, sans-serif;">
//                     <h2>Your OTP Code</h2>
//                     <p>Use the following OTP to verify your email:</p>
//                     <h1 style="color:#4A6CF7;">%s</h1>
//                     <p>This OTP will expire in 5 minutes.</p>
//                   </body>
//                 </html>
//                 """.formatted(otp);
//     }
// }

package com.nguyenanhbinh.lab306new.service.impl;

import com.nguyenanhbinh.lab306new.service.EmailService;
import com.resend.*;
import com.resend.services.emails.model.SendEmailRequest;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private Resend resend;
    private final String apiKey = System.getenv("RESEND_API_KEY");
    private final String from = System.getenv().getOrDefault(
            "MAIL_FROM", "onboarding@resend.dev");

    @PostConstruct
    void init() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("❌ RESEND_API_KEY is missing");
        }
        resend = new Resend(apiKey);
        log.info("✅ Resend email service initialized");
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SendEmailRequest email = SendEmailRequest.builder()
                    .from(from)
                    .to(toEmail)
                    .subject("Your OTP Verification Code")
                    .html(buildTemplate(otp))
                    .build();

            resend.emails().send(email);
            log.info("📧 OTP email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("❌ Failed to send OTP email to {}", toEmail, e);
            throw new RuntimeException("Failed to send OTP email");
        }
    }

    private String buildTemplate(String otp) {
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
