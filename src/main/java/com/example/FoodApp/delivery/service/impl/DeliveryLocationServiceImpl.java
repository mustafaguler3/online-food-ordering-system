package com.example.FoodApp.delivery.service.impl;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.auth_users.services.UserService;
import com.example.FoodApp.delivery.dto.UpdateLocationDTO;
import com.example.FoodApp.delivery.entity.DeliveryLocation;
import com.example.FoodApp.delivery.entity.DeliveryPerson;
import com.example.FoodApp.delivery.repository.DeliveryLocationRepository;
import com.example.FoodApp.delivery.repository.DeliveryPersonRepository;
import com.example.FoodApp.delivery.service.DeliveryLocationService;
import com.example.FoodApp.exceptions.NotFoundException;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.repository.OrderRepository;
import com.example.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryLocationServiceImpl implements DeliveryLocationService {

    private final DeliveryPersonRepository deliveryPersonRepository;
    private final DeliveryLocationRepository deliveryLocationRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    @Override
    public Response<?> updateLocation(Long deliveryId,
            Double lat,
            Double lng
    ) {
        DeliveryPerson dp = deliveryPersonRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));

        DeliveryLocation loc = new DeliveryLocation();
        loc.setDeliveryPerson(dp);
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        loc.setTimestamp(LocalDateTime.now());

        deliveryLocationRepository.save(loc);

        dp.setCurrentLat(lat);
        dp.setCurrentLng(lng);
        deliveryPersonRepository.save(dp);

        return Response.builder()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .message("Updated")
                .build();
    }

    @Override
    public Response<DeliveryLocation> getLatestLocation(Long deliveryPersonId) {
        DeliveryLocation latest = deliveryLocationRepository
                .findFirstByDeliveryPersonIdOrderByTimestampDesc(deliveryPersonId)
                .orElseThrow(() -> new NotFoundException("No location found for delivery person " + deliveryPersonId));

        return Response.<DeliveryLocation>builder()
                .statusCode(HttpStatus.OK.value())
                .data(latest)
                .build();
    }

    @Override
    public Response<?> startLocation(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        DeliveryPerson dp = order.getDeliveryPerson();

        if (dp == null) {
            User currentUser = userService.getCurrentLoggedInUser();
            dp = currentUser.getDeliveryPerson();

            if (dp == null) {
                throw new RuntimeException("Current user is not a delivery person");
            }

            order.setDeliveryPerson(dp);
            orderRepository.save(order);
        }

        dp.setCurrentLat(order.getRestaurant().getLatitude());
        dp.setCurrentLng(order.getRestaurant().getLongitude());
        deliveryPersonRepository.save(dp);

        DeliveryLocation loc = new DeliveryLocation();
        loc.setDeliveryPerson(dp);
        loc.setLatitude(dp.getCurrentLat());
        loc.setLongitude(dp.getCurrentLng());
        loc.setTimestamp(LocalDateTime.now());
        deliveryLocationRepository.save(loc);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Delivery started")
                .build();
    }
}
