package com.creatorsstudio.service.impl;

import com.creatorsstudio.dto.request.UserLoginRequest;
import com.creatorsstudio.dto.request.UserRegisterRequest;
import com.creatorsstudio.dto.response.UserResponse;
import com.creatorsstudio.entity.User;
import com.creatorsstudio.exception.DuplicateUsernameException;
import com.creatorsstudio.exception.ValidationException;
import com.creatorsstudio.repository.UserRepository;
import com.creatorsstudio.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserResponse registerUser(UserRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateUsernameException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .build();
    }

    @Override
    public UserResponse loginUser(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ValidationException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ValidationException("Invalid username or password");
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
