package com.example.FoodApp.auth_users.dtos;

import com.example.FoodApp.auth_users.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private UserDTO user;
}
