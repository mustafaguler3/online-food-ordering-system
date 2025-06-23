package com.example.FoodApp.role.controller;

import com.example.FoodApp.response.Response;
import com.example.FoodApp.role.dtos.RoleDTO;
import com.example.FoodApp.role.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController {

    private final RoleService roleService;
    @PostMapping
    public ResponseEntity<Response<RoleDTO>> createRole(@RequestBody @Valid RoleDTO roleDTO) {
        return ResponseEntity.ok(roleService.createRole(roleDTO));
    }
    @PostMapping
    public ResponseEntity<Response<RoleDTO>> updateRole(@RequestBody @Valid RoleDTO roleDTO) {
        return ResponseEntity.ok(roleService.updateRole(roleDTO));
    }
    @PostMapping
    public ResponseEntity<Response<List<RoleDTO>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteRole(@PathVariable long id) {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }
}



















