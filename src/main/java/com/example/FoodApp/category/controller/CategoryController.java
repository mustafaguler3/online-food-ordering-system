package com.example.FoodApp.category.controller;

import com.example.FoodApp.category.dtos.CategoryDTO;
import com.example.FoodApp.category.services.CategoryService;
import com.example.FoodApp.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<CategoryDTO>> addCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.addCategory(categoryDTO));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<CategoryDTO>> updateCategory(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<CategoryDTO>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<CategoryDTO>>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }
}

























