package com.creatorsstudio.controller;

import com.creatorsstudio.dto.request.UserLoginRequest;
import com.creatorsstudio.dto.request.UserRegisterRequest;
import com.creatorsstudio.dto.response.UserResponse;
import com.creatorsstudio.exception.ApiResponse;
import com.creatorsstudio.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse response = authService.registerUser(request);
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(response)
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserResponse response = authService.loginUser(request);
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User logged in successfully")
                .data(response)
                .build();
    }
}
