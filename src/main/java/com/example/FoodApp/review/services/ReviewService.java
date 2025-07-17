package com.example.FoodApp.review.services;

import com.example.FoodApp.response.Response;
import com.example.FoodApp.review.dtos.ReviewDTO;

import java.util.List;

public interface ReviewService {
    Response<ReviewDTO> createReview(ReviewDTO reviewDTO);
    Response<List<ReviewDTO>> getReviewsForMenu(Long menuId);
    Response<Double> getAverageRating(Long menuId);
}
