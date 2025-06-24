package com.example.FoodApp.auth_users.services;

import com.example.FoodApp.auth_users.dtos.UserDTO;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.response.Response;

import java.util.List;

public interface UserService {
    User getCurrentLoggedInUser();
    Response<List<UserDTO>> getAllUser();
    Response<UserDTO> getOwnAccountDetails();
    Response<?> updateOwnAccount(UserDTO userDTO);
    Response<?> deactivateOwnAccount();
}
