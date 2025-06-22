package com.example.FoodApp.review.dtos;

import com.example.FoodApp.auth_users.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewDTO {
    private long id;
    private long menuId;
    private long orderId;
    private String userName;
    private String menuName;
    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(10)
    private Integer rating;
    @Size(max = 500,message = "Comment cannot exceed 500 characters")
    private String comment;
    private LocalDateTime createdAt;

}


















