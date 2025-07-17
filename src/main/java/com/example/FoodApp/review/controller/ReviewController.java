package com.example.FoodApp.review.controller;

import com.example.FoodApp.response.Response;
import com.example.FoodApp.review.dtos.ReviewDTO;
import com.example.FoodApp.review.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response<ReviewDTO>> createReview(@RequestBody @Valid ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.createReview(reviewDTO));
    }

    @GetMapping("/menu-item/{menuId}")
    public ResponseEntity<Response<List<ReviewDTO>>> getReviewsForMeny(@PathVariable Long menuId) {
        return ResponseEntity.ok(reviewService.getReviewsForMenu(menuId));
    }

    @GetMapping("/menu-item/average/{menuId}")
    public ResponseEntity<Response<Double>> getAverageRating(@PathVariable Long menuId) {
        return ResponseEntity.ok(reviewService.getAverageRating(menuId));
    }
}























