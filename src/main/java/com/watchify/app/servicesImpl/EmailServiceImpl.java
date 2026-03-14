package com.watchify.app.servicesImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.watchify.app.exceptions.EmailNotVerifiedException;
import com.watchify.app.services.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("WebFlix - Verify Your Email");

            String verificationLink = frontendUrl + "/api/auth/verify-email?token=" + token;
            String emailBody
                    = "Welcome to Webflix !\n\n"
                    + "Thank you for registering. Please verify your email address by clicking the link below:\n\n"
                    + verificationLink + "\n\n"
                    + "This link will expire in 24 hours.\n\n"
                    + "If you didn't create this account, please ignore this email.\n\n"
                    + "Best regards,\n"
                    + "Webflix Team";

            message.setText(emailBody);
            mailSender.send(message);
            logger.info("Verification email sent to {} ", toEmail);

        } catch (Exception ex) {
            logger.error("Failed to send verification email to {}: {}", toEmail, ex.getMessage(), ex);
            throw new EmailNotVerifiedException("Failed to send verification email");
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("WebFlix - Reset Password");

            String resetPasswordLink = frontendUrl + "/reset-password?token=" + token;
            String emailBody
                    = "Hello,\n\n"
                    + "We received a request to reset your password.\n\n"
                    + "Please click the link below to create a new password:\n\n"
                    + resetPasswordLink + "\n\n"
                    + "This link will expire in 24 hours for your security.\n\n"
                    + "If you did not request a password reset, please ignore this email.\n\n"
                    + "Best regards,\n"
                    + "Webflix Team";

            message.setText(emailBody);
            mailSender.send(message);
            logger.info("Password Reset email sent to {} ", toEmail);

        } catch (Exception ex) {
            logger.error("Failed to send password reset email to {}: {}", toEmail, ex.getMessage(), ex);
            throw new EmailNotVerifiedException("Failed to send password reset email");
        }
    }

}
