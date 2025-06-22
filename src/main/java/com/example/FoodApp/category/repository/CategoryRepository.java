package com.example.FoodApp.category.repository;

import com.example.FoodApp.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
