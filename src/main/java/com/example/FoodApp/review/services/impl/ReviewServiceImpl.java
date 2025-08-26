package com.example.FoodApp.review.services.impl;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.auth_users.services.UserService;
import com.example.FoodApp.config.DtoConverter;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.exceptions.BadRequestException;
import com.example.FoodApp.exceptions.NotFoundException;
import com.example.FoodApp.menu.entity.Menu;
import com.example.FoodApp.menu.repository.MenuRepository;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.repository.OrderItemRepository;
import com.example.FoodApp.order.repository.OrderRepository;
import com.example.FoodApp.response.Response;
import com.example.FoodApp.review.dtos.ReviewDTO;
import com.example.FoodApp.review.entity.Review;
import com.example.FoodApp.review.repository.ReviewRepository;
import com.example.FoodApp.review.services.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final DtoConverter dtoConverter;

    @Override
    public Response<ReviewDTO> createReview(ReviewDTO reviewDTO) {
        User user = userService.getCurrentLoggedInUser();
        //validate required fields
        if (reviewDTO.getOrderId() == null || reviewDTO.getMenuId() == null) {
            throw new BadRequestException("Order Id and Menu Item id are required");
        }
        // Validate menu item exists
        Menu menu = menuRepository.findById(reviewDTO.getMenuId())
                .orElseThrow(() -> new NotFoundException("Menu item not found"));
        // Validate order exists
        Order order = orderRepository.findById(reviewDTO.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // Validate order status is DELIVERED
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("You can only review items from delivered orders");
        }

        // Validate that menu item was part of this order
        boolean itemInOrder = orderItemRepository.existsByOrderIdAndMenuId(reviewDTO.getOrderId(),reviewDTO.getMenuId());

        if (!itemInOrder) {
            throw new BadRequestException("This menu item was not part of the specified order");
        }

        //Check if user already wrote a review for the item
        if (reviewRepository.existsByUserIdAndMenuIdAndOrderId(user.getId(),reviewDTO.getMenuId(),reviewDTO.getOrderId())) {
            throw new BadRequestException("You have already reviewed this item from this order");
        }

        //Create and save review
        Review review = Review.builder()
                .user(user)
                .menu(menu)
                .order(order)
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Review saveReview = reviewRepository.save(review);

        ReviewDTO responseDto = modelMapper.map(saveReview, ReviewDTO.class);
        responseDto.setUserName(user.getName());
        responseDto.setMenuName(menu.getName());

        return Response.<ReviewDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Review added successfully")
                .data(responseDto)
                .build();
    }

    @Override
    public Response<List<ReviewDTO>> getReviewsForMenu(Long menuId) {
        List<Review> reviews = reviewRepository.findByMenuIdOrderByIdDesc(menuId);

        List<ReviewDTO> reviewDTOS = reviews.stream()
                .map(review -> modelMapper.map(review,ReviewDTO.class))
                .toList();

        return Response.<List<ReviewDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Reviews retrieved successfully")
                .data(reviewDTOS)
                .build();
    }

    @Override
    public Response<Double> getAverageRating(Long menuId) {
        Double averageRating = reviewRepository.calculateAverageRatingByMenuId(menuId);

        return Response.<Double>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Average rating retrieved successfully")
                .data(averageRating != null ? averageRating : 0.0)
                .build();
    }
}
