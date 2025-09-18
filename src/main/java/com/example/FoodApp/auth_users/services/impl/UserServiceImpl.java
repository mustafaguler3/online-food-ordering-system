package com.example.FoodApp.auth_users.services.impl;

import com.example.FoodApp.auth_users.dtos.UserDTO;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.auth_users.repository.UserRepository;
import com.example.FoodApp.auth_users.services.UserService;
import com.example.FoodApp.aws.AWSS3Service;
import com.example.FoodApp.email_notification.dtos.NotificationDTO;
import com.example.FoodApp.email_notification.services.NotificationService;
import com.example.FoodApp.exceptions.BadRequestException;
import com.example.FoodApp.exceptions.NotFoundException;
import com.example.FoodApp.response.Response;
import com.example.FoodApp.role.dtos.RoleDTO;
import com.example.FoodApp.role.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    //private final AWSS3Service awss3Service;

    @Override
    public User getCurrentLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("user not found"));
    }
    @Override
    public Response<List<UserDTO>> getAllUser() {
        log.info("Inside getAllUser()");
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        List<UserDTO> userDTOS = modelMapper.map(users,new TypeToken<List<UserDTO>>() {
        }.getType());
        return Response.<List<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All users retrieved successfully")
                .data(userDTOS)
                .build();
    }

    @Override
    public Response<UserDTO> getOwnAccountDetails() {

        User user = getCurrentLoggedInUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<?> updateOwnAccount(UserDTO userDTO,MultipartFile imageFile) throws IOException {
        User user = getCurrentLoggedInUser();

        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "profile").toString();
            Files.createDirectories(Paths.get(uploadDir));

            String imageName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path path = Paths.get(uploadDir, imageName);
            imageFile.transferTo(path.toFile());
            user.setProfileUrl("/uploads/profile/" + imageName);
        }

        if (userDTO.getName() != null) user.setName(userDTO.getName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getPhoneNumber() != null) user.setPhoneNumber(userDTO.getPhoneNumber());

        User updatedUser = userRepository.save(user);

        UserDTO responseDto = new UserDTO();
        responseDto.setId(updatedUser.getId());
        responseDto.setName(updatedUser.getName());
        responseDto.setEmail(updatedUser.getEmail());
        responseDto.setPhoneNumber(updatedUser.getPhoneNumber());
        responseDto.setProfileUrl(updatedUser.getProfileUrl());

        List<RoleDTO> rolesDto = updatedUser.getRoles() != null
                ? updatedUser.getRoles().stream()
                .map(role -> {
                    RoleDTO dto = new RoleDTO();
                    dto.setId(role.getId());
                    dto.setName(role.getName());
                    return dto;
                })
                .collect(Collectors.toList())
                : Collections.emptyList();

        responseDto.setRoles(rolesDto);
        responseDto.setActive(updatedUser.isActive());

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account updated successfully")
                .data(responseDto)
                .build();
    }
    @Override
    public Response<?> deactivateOwnAccount() {
        User user = getCurrentLoggedInUser();
        user.setActive(false);
        userRepository.save(user);
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Account Deactivated")
                .body("Your account has been deactivated. If this was a mistake, please contact support")
                .build();

        notificationService.sendEmail(notificationDTO);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account deactivated successfully")
                .build();
    }

    @Override
    public Response<List<UserDTO>> findUsersByRoleDeliveries() {
        List<User> users = userRepository.findByRoles_Name("DELIVERY");

        if (users.isEmpty()) {
            throw new RuntimeException("Not Found Delivery");
        }

        List<UserDTO> userDTOS = users.stream().map(delivery -> modelMapper.map(delivery,UserDTO.class))
                .toList();
        return Response.<List<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .data(userDTOS)
                .build();
    }
}
















