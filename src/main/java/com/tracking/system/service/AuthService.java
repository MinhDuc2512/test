package com.tracking.system.service;

import com.tracking.system.dto.request.LoginRequest;
import com.tracking.system.dto.request.RegisterRequest;
import com.tracking.system.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse getCurrentUser(String username);
}
