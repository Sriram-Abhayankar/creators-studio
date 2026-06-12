package com.creatorsstudio.service;

import com.creatorsstudio.dto.request.UserLoginRequest;
import com.creatorsstudio.dto.request.UserRegisterRequest;
import com.creatorsstudio.dto.response.UserResponse;

public interface AuthService {
    UserResponse registerUser(UserRegisterRequest request);
    UserResponse loginUser(UserLoginRequest request);
}
