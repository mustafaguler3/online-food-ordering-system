package com.example.FoodApp.role.services;

import com.example.FoodApp.response.Response;
import com.example.FoodApp.role.dtos.RoleDTO;

import java.util.List;

public interface RoleService {
    Response<RoleDTO> createRole(RoleDTO roleDTO);
    Response<RoleDTO> updateRole(RoleDTO roleDTO);
    Response<List<RoleDTO>> getAllRoles();
    Response<?> deleteRole(Long id);
}
