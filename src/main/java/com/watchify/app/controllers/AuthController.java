package com.watchify.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.watchify.app.requests.ChangePasswordRequest;
import com.watchify.app.requests.EmailRequest;
import com.watchify.app.requests.LoginRequest;
import com.watchify.app.requests.ResetPasswordRequest;
import com.watchify.app.requests.UserRequest;
import com.watchify.app.responses.EmailValidationResponse;
import com.watchify.app.responses.LoginResponse;
import com.watchify.app.responses.MessageResponse;
import com.watchify.app.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signUp(@Valid @RequestBody UserRequest userRequest) {

        return ResponseEntity.ok(authService.signUp(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-email")
    public ResponseEntity<EmailValidationResponse> validateEmail(@RequestParam String email) {
        return ResponseEntity.ok(authService.validateEmail(email));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerification(@Valid @RequestBody EmailRequest emailRequest) {
        return ResponseEntity.ok(authService.resendVerification(emailRequest.getEmail()));

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody EmailRequest emailRequest) {
        return ResponseEntity.ok(authService.forgotPassword(emailRequest.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(authService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        String email = authentication.getName();
        return ResponseEntity.ok(authService.changePassword(
                email,
                changePasswordRequest.getCurrentPassword(),
                changePasswordRequest.getNewPassword()
        ));
    }

    @GetMapping("current-user")
    public ResponseEntity<LoginResponse> currentUser(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(authService.currentUser(email));
    }

}
