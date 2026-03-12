package com.watchify.app.servicesImpl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.watchify.app.dao.UserRepository;
import com.watchify.app.enums.Role;
import com.watchify.app.exceptions.AccountDeactivatedException;
import com.watchify.app.exceptions.BadCredentialsException;
import com.watchify.app.exceptions.EmailAlreadyExistsException;
import com.watchify.app.exceptions.EmailNotVerifiedException;
import com.watchify.app.exceptions.InvalidCredentialsException;
import com.watchify.app.exceptions.InvalidTokenException;
import com.watchify.app.models.User;
import com.watchify.app.requests.UserRequest;
import com.watchify.app.responses.EmailValidationResponse;
import com.watchify.app.responses.LoginResponse;
import com.watchify.app.responses.MessageResponse;
import com.watchify.app.security.JWTUtils;
import com.watchify.app.services.AuthService;
import com.watchify.app.services.EmailService;
import com.watchify.app.utils.ServiceUtils;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JWTUtils jwtUtil;

    @Autowired
    private ServiceUtils serviceUtils;

    @Override
    public MessageResponse signUp(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.USER);
        user.setActive(true);
        user.setEmailVeified(false);
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
        userRepository.save(user);

        emailService.sendVerificationEmail(userRequest.getEmail(), verificationToken);

        return new MessageResponse("Registration successful! Please check your email to verify your account");
    }

    @Override
    public LoginResponse login(String email, String password) {
        User user = userRepository
                .findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        if (!user.isActive()) {
            throw new AccountDeactivatedException("Your account has been deactivated. Please contact support for assistnce");
        }
        if (!user.isEmailVeified()) {
            throw new EmailNotVerifiedException("Please verify your email address before login in. Check your inbox for the verification link.");
        }

        final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Override
    public EmailValidationResponse validateEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        return new EmailValidationResponse(exists, !exists);
    }

    @Override
    public MessageResponse verifyEmail(String token) {
        // TODO Auto-generated method stub
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token."));

        if (user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(Instant.now())) {
            throw new InvalidTokenException("Verification link has expired. Please report a new one.");
        }

        user.setEmailVeified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Email verified successfully. you can login now.");
    }

    @Override
    public MessageResponse resendVerification(String email) {

        User user = serviceUtils.getUserByEmailOrThrow(email);

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
        userRepository.save(user);
        emailService.sendVerificationEmail(email, verificationToken);

        return new MessageResponse("Verification email resent successfully! Please check your inbox.");
    }

    @Override
    public MessageResponse forgotPassword(String email) {

        User user = serviceUtils.getUserByEmailOrThrow(email);
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(3600));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, resetToken);
        return new MessageResponse("Password reset email sent successfully! Please check your inbox.");
    }

    @Override
    public MessageResponse resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token).orElseThrow(()
                -> new InvalidTokenException("Ivalid or expired token reset"));
        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(Instant.now())) {
            throw new InvalidTokenException("Reset token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);

        return new MessageResponse("Password reset successfully! You can now log in with new account.");
    }

    @Override
    public MessageResponse changePassword(String email, String currentPassword, String newPassword) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new MessageResponse("Password changed successfully");
    }

    @Override
    public LoginResponse currentUser(String email) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        return new LoginResponse(null, user.getEmail(), user.getFullName(), user.getRole().name());
    }

}
