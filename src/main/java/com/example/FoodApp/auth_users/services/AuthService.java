package com.example.FoodApp.auth_users.services;

import com.example.FoodApp.auth_users.dtos.LoginRequest;
import com.example.FoodApp.auth_users.dtos.LoginResponse;
import com.example.FoodApp.auth_users.dtos.RegistrationRequest;
import com.example.FoodApp.response.Response;

public interface AuthService {
    Response<?> register(RegistrationRequest registrationRequest);
    Response<LoginResponse> login(LoginRequest loginRequest);
}
