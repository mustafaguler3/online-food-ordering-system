package com.example.FoodApp.auth_users.services;

import com.example.FoodApp.auth_users.dtos.UserDTO;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User getCurrentLoggedInUser();
    Response<List<UserDTO>> getAllUser();
    Response<UserDTO> getOwnAccountDetails();
    Response<?> updateOwnAccount(UserDTO userDTO, MultipartFile imageFile) throws IOException;
    Response<?> deactivateOwnAccount();
    Response<List<UserDTO>> findUsersByRoleDeliveries();
}
