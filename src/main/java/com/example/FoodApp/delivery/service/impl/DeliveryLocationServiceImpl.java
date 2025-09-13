package com.example.FoodApp.delivery.service.impl;

import com.example.FoodApp.delivery.entity.DeliveryLocation;
import com.example.FoodApp.delivery.entity.DeliveryPerson;
import com.example.FoodApp.delivery.repository.DeliveryLocationRepository;
import com.example.FoodApp.delivery.repository.DeliveryPersonRepository;
import com.example.FoodApp.delivery.service.DeliveryLocationService;
import com.example.FoodApp.exceptions.NotFoundException;
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
    @Override
    public Response<?> updateLocation(Long deliveryPersonId, double lat, double lng) {
        DeliveryPerson dp = deliveryPersonRepository.findById(deliveryPersonId)
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
}
