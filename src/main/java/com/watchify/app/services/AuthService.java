package com.watchify.app.services;

import com.watchify.app.requests.UserRequest;
import com.watchify.app.responses.EmailValidationResponse;
import com.watchify.app.responses.LoginResponse;
import com.watchify.app.responses.MessageResponse;

import jakarta.validation.Valid;

public interface AuthService {

    MessageResponse signUp(@Valid UserRequest userRequest);

    LoginResponse login(String email, String password);

    EmailValidationResponse validateEmail(String email);

    MessageResponse verifyEmail(String token);

    MessageResponse resendVerification(String email);

    MessageResponse forgotPassword(String email);

    MessageResponse resetPassword(String token, String newPassword);

    MessageResponse changePassword(String email, String currentPassword, String newPassword);

    LoginResponse currentUser(String email);

}
