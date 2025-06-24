package com.example.FoodApp.auth_users.services.impl;

import com.example.FoodApp.auth_users.dtos.LoginRequest;
import com.example.FoodApp.auth_users.dtos.LoginResponse;
import com.example.FoodApp.auth_users.dtos.RegistrationRequest;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.auth_users.repository.UserRepository;
import com.example.FoodApp.auth_users.services.AuthService;
import com.example.FoodApp.exceptions.BadRequestException;
import com.example.FoodApp.exceptions.NotFoundException;
import com.example.FoodApp.response.Response;
import com.example.FoodApp.role.entity.Role;
import com.example.FoodApp.role.repository.RoleRepository;
import com.example.FoodApp.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;

    @Override
    public Response<?> register(RegistrationRequest registrationRequest) {
        log.info("Inside register()");

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        List<Role> userRoles;

        if (registrationRequest.getRoles() != null && !registrationRequest.getRoles().isEmpty()) {
            userRoles = registrationRequest.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName.toUpperCase())
                            .orElseThrow(() -> new NotFoundException("Role with name: "+roleName+" not found")))
                    .toList();
        }else {
            Role defaultRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new NotFoundException("Default CUSTOMER role not found"));

            userRoles = List.of(defaultRole);
        }
        User userToSave = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .address(registrationRequest.getAddress())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(userRoles)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(userToSave);
        log.info("user registration successfully");
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User Registered Successfully")
                .build();
    }

    @Override
    public Response<?> login(LoginRequest loginRequest) {

        log.info("Inside login()");

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Invalid Email"));

        if (!user.isActive()) {
            throw new NotFoundException("Account not active, Please contact customer support");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())) {
            throw new BadRequestException("Invalid Password");
        }

        String token = jwtUtils.generateToken(user.getEmail());

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setRoles(roleNames);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login Successfully")
                .data(loginResponse)
                .build();
    }
}

















