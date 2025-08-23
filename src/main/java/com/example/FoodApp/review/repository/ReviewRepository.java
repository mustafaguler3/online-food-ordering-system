package com.example.FoodApp.review.repository;

import com.example.FoodApp.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByMenuIdOrderByIdDesc(Long menuId);

    @Query("select avg(r.rating) from Review r where r.menu.id = :menuId")
    Double calculateAverageRatingByMenuId(@Param("menuId") long menuId);

    @Query("select case when count(r) > 0 then true else false end " +
            "from Review r " +
            "where r.user.id = :userId AND r.menu.id = :menuId AND r.orderId = :orderId")
    boolean existsByUserIdAndMenuIdAndOrderId(@Param("userId") long userId,
                                              @Param("menuId") long menuId,
                                              @Param("orderId") long orderId);

}
