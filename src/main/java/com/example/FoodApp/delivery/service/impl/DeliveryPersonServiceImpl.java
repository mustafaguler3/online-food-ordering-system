package com.example.FoodApp.delivery.service.impl;

import com.example.FoodApp.delivery.dto.DeliveryPersonDTO;
import com.example.FoodApp.delivery.entity.DeliveryPerson;
import com.example.FoodApp.delivery.repository.DeliveryPersonRepository;
import com.example.FoodApp.delivery.service.DeliveryPersonService;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.repository.OrderRepository;
import com.example.FoodApp.response.Response;
import com.example.FoodApp.security.AuthUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryPersonServiceImpl implements DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;

    @Override
    public Response<List<DeliveryPersonDTO>> findAllDeliveries() {
        List<DeliveryPerson> deliveryPeople = deliveryPersonRepository.findAll();

        if (deliveryPeople.isEmpty()) {
            throw new RuntimeException("No delivery person");
        }
        List<DeliveryPersonDTO> deliveryPersonDTOS =
                deliveryPeople.stream().map(delivery -> modelMapper.map(delivery,DeliveryPersonDTO.class))
                        .toList();

        return Response.<List<DeliveryPersonDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .data(deliveryPersonDTOS)
                .build();
    }

}
