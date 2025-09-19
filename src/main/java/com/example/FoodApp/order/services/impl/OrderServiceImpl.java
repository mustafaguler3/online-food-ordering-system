package com.example.FoodApp.order.services.impl;

import com.example.FoodApp.auth_users.entity.Address;
import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.auth_users.repository.AddressRepository;
import com.example.FoodApp.auth_users.repository.UserRepository;
import com.example.FoodApp.auth_users.services.UserService;
import com.example.FoodApp.cart.entity.Cart;
import com.example.FoodApp.cart.entity.CartItem;
import com.example.FoodApp.cart.repository.CartItemRepository;
import com.example.FoodApp.cart.repository.CartRepository;
import com.example.FoodApp.cart.services.CartService;
import com.example.FoodApp.config.DtoConverter;
import com.example.FoodApp.config.OrderMapper;
import com.example.FoodApp.delivery.dto.DailyEarningDTO;
import com.example.FoodApp.delivery.dto.DashboardDTO;
import com.example.FoodApp.delivery.dto.DeliveryPersonDTO;
import com.example.FoodApp.delivery.entity.DeliveryLocation;
import com.example.FoodApp.delivery.entity.DeliveryPerson;
import com.example.FoodApp.delivery.repository.DeliveryLocationRepository;
import com.example.FoodApp.delivery.repository.DeliveryPersonRepository;
import com.example.FoodApp.email_notification.dtos.NotificationDTO;
import com.example.FoodApp.email_notification.services.NotificationService;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.enums.PaymentStatus;
import com.example.FoodApp.exceptions.BadRequestException;
import com.example.FoodApp.exceptions.NotFoundException;
import com.example.FoodApp.menu.dtos.MenuDTO;
import com.example.FoodApp.menu.entity.Menu;
import com.example.FoodApp.order.dtos.OrderDTO;
import com.example.FoodApp.order.dtos.OrderItemDTO;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.entity.OrderItem;
import com.example.FoodApp.order.repository.OrderItemRepository;
import com.example.FoodApp.order.repository.OrderRepository;
import com.example.FoodApp.order.services.OrderService;
import com.example.FoodApp.response.Response;
import com.example.FoodApp.restaurant.entity.Restaurant;
import com.example.FoodApp.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final DtoConverter dtoConverter;
    private final UserRepository userRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final DeliveryLocationRepository deliveryLocationRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;

    @Value("${base.payment.link}")
    private String basePaymentLink;

    @Override
    public Response<?> placeOrderFromCart() {

        try {
            User customer = userService.getCurrentLoggedInUser();

            Address deliveryAddress = addressRepository.findAddressByUserId(customer.getId());

            if (deliveryAddress == null) {
                throw new NotFoundException("Delivery Address not present for the user");
            }

            Cart cart = cartRepository.findByUser_Id(customer.getId())
                    .orElseThrow(() -> new NotFoundException("Cart not found for the user"));

            List<CartItem> cartItems = cart.getCartItems();

            if (cartItems == null || cartItems.isEmpty()) throw new BadRequestException("Cart is empty");

            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = OrderItem.builder()
                        .menu(cartItem.getMenu())
                        .restaurant(cartItem.getMenu().getRestaurant())
                        .quantity(cartItem.getQuantity())
                        .pricePerUnit(cartItem.getPricePerUnit())
                        .subtotal(cartItem.getSubtotal())
                        .build();

                orderItems.add(orderItem);
                totalAmount = totalAmount.add(orderItem.getSubtotal());
            }
            Set<Restaurant> restaurants = cartItems.stream()
                    .map(ci -> ci.getMenu().getRestaurant())
                    .collect(Collectors.toSet());

            Restaurant restaurant = restaurants.iterator().next();

            String orderCode = UUID.randomUUID().toString();

            Order order = Order.builder()
                    .user(customer)
                    .orderItems(orderItems)
                    .orderCode(orderCode.substring(0,6))
                    .orderDate(LocalDateTime.now())
                    .totalAmount(totalAmount)
                    .orderStatus(OrderStatus.INITIALIZED)
                    .paymentStatus(PaymentStatus.PENDING)
                    .deliveryAddress(deliveryAddress)
                    .restaurant(restaurant)
                    .deliveryPerson(null)
                    .build();


            Order savedOrder = orderRepository.save(order);

            orderItems.forEach(orderItem -> orderItem.setOrder(savedOrder));
            orderItemRepository.saveAll(orderItems);

            // clear cart
            cartService.clearShoppingCart();

            //OrderDTO orderDTO = dtoConverter.toOrderDto(savedOrder);
            OrderDTO orderDTO = modelMapper.map(order,OrderDTO.class);

            //autoAssignDeliveryPerson(deliveryPersonId);
            //send email
            sendOrderConfirmationEmail(customer, orderDTO);

            return Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Your order has been received! We've sent a secure payment link to your email. Please complete the payment to confirm your order.")
                    .build();

        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void sendOrderConfirmationEmail(User customer, OrderDTO orderDTO) {
        String subject = "Your order confirmation Order #"+orderDTO.getId();

        Context context = new Context(Locale.getDefault());
        context.setVariable("customerName",customer.getName());
        context.setVariable("orderId",String.valueOf(orderDTO.getId()));
        context.setVariable("orderDate",orderDTO.getOrderDate().toString());
        context.setVariable("totalAmount",orderDTO.getTotalAmount().toString());
        //format delivery address
        String deliveryAddress = orderDTO.getUser().getAddress();
        context.setVariable("deliveryAddress",deliveryAddress);
        context.setVariable("currentYear", Year.now());

        // build order items HTML using StringBuilder
        StringBuilder orderItemsHtml = new StringBuilder();

        for (OrderItemDTO item: orderDTO.getOrderItems()) {
            orderItemsHtml.append("<div class=\"order-item\">")
                    .append("<p>")
                    .append(item.getMenu().getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append("</p>")
                    .append("<p> $")
                    .append(item.getSubtotal())
                    .append("</p>")
                    .append("</div>");
        }
        context.setVariable("orderItemsHtml",orderItemsHtml.toString());
        context.setVariable("totalItems",orderDTO.getOrderItems().size());

        String paymentLink = basePaymentLink  + orderDTO.getId() + "&amount="+orderDTO.getTotalAmount();
        context.setVariable("paymentLink",paymentLink);

        String emailBody = templateEngine.process("order-confirmation",context);

        notificationService.sendEmail(NotificationDTO.builder()
                .recipient(customer.getEmail())
                .subject(subject)
                .body(emailBody)
                .isHtml(true)
                .build());
    }

    @Override
    public Response<OrderDTO> getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));

        OrderDTO orderDTO = modelMapper.map(order,OrderDTO.class);

        return Response.<OrderDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order retrieved successfully").data(orderDTO).build();
    }

    @Override
    public Response<Page<OrderDTO>> getAllOrders(OrderStatus orderStatus, int page, int size) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"id"));

        Page<Order> orderPage;

        if (orderStatus != null) {
            orderPage = orderRepository.findByOrderStatus(orderStatus,pageable);
        }else {
            orderPage = orderRepository.findAll(pageable);
        }

        Page<OrderDTO> orderDTOPage = orderPage.map(order -> {
            OrderDTO dto = modelMapper.map(order,OrderDTO.class);
            dto.getOrderItems()
                    .forEach(orderItemDTO -> orderItemDTO.getMenu().setReviews(null));
            return dto;
        });

        return Response.<Page<OrderDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Orders retrieved successfully")
                .data(orderDTOPage)
                .build();
    }

    @Override
    public Response<List<OrderDTO>> getOrdersOfUser() {

        User customer = userService.getCurrentLoggedInUser();
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(customer);

        List<OrderDTO> orderDTOS = orders.stream()
                .map(order -> modelMapper.map(order,OrderDTO.class))
                .toList();

        orderDTOS.forEach(orderItem -> {
            orderItem.setUser(null);
            orderItem.getOrderItems().forEach(item -> item.getMenu().setReviews(null));
        });

        return Response.<List<OrderDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Orders for user retrieved successfully")
                .data(orderDTOS)
                .build();
    }

    @Override
    public Response<OrderItemDTO> getOrderItemById(Long orderItemId) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order item not found"));

        OrderItemDTO orderItemDTO = modelMapper.map(orderItem,OrderItemDTO.class);

        orderItemDTO.setMenu(modelMapper.map(orderItem.getMenu(), MenuDTO.class));

        return Response.<OrderItemDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("OrderItem retrieved successfully")
                .data(orderItemDTO)
                .build();
    }

    @Override
    public Response<OrderDTO> updateOrderStatus(OrderDTO orderDTO) {

        Order order = orderRepository.findById(orderDTO.getId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        OrderStatus orderStatus = orderDTO.getOrderStatus();
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        DeliveryPerson dp = order.getDeliveryPerson();
        Restaurant restaurant = order.getRestaurant();

        if (OrderStatus.ON_THE_WAY.name().equals(orderStatus.name())) {

            if (dp == null) {
                throw new RuntimeException("Delivery person not assigned to this order.");
            }

            if (restaurant == null) {
                throw new RuntimeException("Restaurant info not found for this order.");
            }

            DeliveryLocation loc = new DeliveryLocation();
            loc.setDeliveryPerson(dp);
            loc.setLatitude(restaurant.getLatitude());
            loc.setLongitude(restaurant.getLongitude());
            loc.setTimestamp(LocalDateTime.now());

            deliveryLocationRepository.save(loc);

            dp.setCurrentLat(restaurant.getLatitude());
            dp.setCurrentLng(restaurant.getLongitude());
            deliveryPersonRepository.save(dp);
        }



        return Response.<OrderDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order status updated successfully")
                .build();
    }

    @Override
    public Response<Long> countUniqueCustomers() {
        long uniqueCustomerCount = orderRepository.countDistinctUsers();
        return Response.<Long>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Unique customer count retrieved successfully")
                .data(uniqueCustomerCount)
                .build();
    }

    @Override
    public Response<?> autoAssignDeliveryPerson(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getDeliveryPerson() != null) {
            return Response.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Delivery person already assigned")
                    .build();
        }

        List<DeliveryPerson> availableDeliveries = deliveryPersonRepository.findByHasActiveOrderFalse();
        if (availableDeliveries.isEmpty()) {
            return Response.builder()
                    .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                    .message("No available deliveries right now")
                    .build();
        }

        DeliveryPerson selected =
                availableDeliveries.get(new Random().nextInt(availableDeliveries.size()));

        order.setDeliveryPerson(selected);
        order.setOrderStatus(OrderStatus.ASSIGNED);
        orderRepository.save(order);

        selected.setHasActiveOrder(true);
        deliveryPersonRepository.save(selected);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order auto-assigned to delivery person: " + selected.getUser().getName())
                .build();
    }

    @Override
    public Response<?> manuelAssignDeliveryPerson(Long orderId, Long deliveryId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getDeliveryPerson() != null) {
            return Response.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Delivery person already assigned")
                    .build();
        }

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));

        if (deliveryPerson.isHasActiveOrder()) {
            return Response.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Selected delivery person already has an active order")
                    .build();
        }

        order.setDeliveryPerson(deliveryPerson);
        order.setOrderStatus(OrderStatus.ASSIGNED);
        orderRepository.save(order);

        deliveryPerson.setHasActiveOrder(true);
        deliveryPersonRepository.save(deliveryPerson);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order manually assigned to delivery person: " + deliveryPerson.getUser().getName())
                .build();
    }

    @Override
    public Response<List<OrderDTO>> getAssignedOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser currentUser = (AuthUser) authentication.getPrincipal();
        List<Order> orders = orderRepository.findByDeliveryPerson_Id(currentUser.getUser().getDeliveryPerson().getId());
        
        /*List<OrderDTO> orderDTOS =
                orders
                        .stream()
                        .map(order -> modelMapper.map(order,OrderDTO.class))
                .toList(); */
        List<OrderDTO> orderDTOS = orderMapper.toOrderDtoList(orders);



        return Response.<List<OrderDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All orders assigned")
                .data(orderDTOS)
                .build();
    }


    @Override
    public Response<OrderDTO> updateOrderStatus(Long orderId,String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser currentUser = (AuthUser) authentication.getPrincipal();
        DeliveryPerson deliveryPerson =
                deliveryPersonRepository.findDeliveryPersonById(currentUser.getUser().getDeliveryPerson().getId());

        if (deliveryPerson == null) {
            throw new RuntimeException("Delivery person not found for current user.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getDeliveryPerson() == null) {
            throw new RuntimeException("No delivery person assigned to this order. Please contact admin.");
        }

        if (!order.getDeliveryPerson().getId().equals(deliveryPerson.getId())) {
            throw new RuntimeException("Not authorized for this order");
        }

        OrderStatus newStatus = OrderStatus.valueOf(status);
        order.setOrderStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);

            DeliveryPerson dp = order.getDeliveryPerson();
            dp.setHasActiveOrder(false);
            deliveryPersonRepository.save(dp);
        }

        if (newStatus == OrderStatus.ON_THE_WAY) {
            deliveryPerson.setHasActiveOrder(true);
            deliveryPersonRepository.save(deliveryPerson);
        }

        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);

        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

        return Response.<OrderDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order status updated successfully")
                .data(orderDTO)
                .build();
    }

    @Override
    public Response<OrderDTO> getAssignedOrderById(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser currentUser = (AuthUser) authentication.getPrincipal();

        Order order = orderRepository.findByIdAndDeliveryPerson_Id(
                orderId,
                currentUser.getUser().getDeliveryPerson().getId()
        ).orElseThrow(() -> new RuntimeException("Order not found or not assigned to this courier"));

        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

        return Response.<OrderDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Assigned order fetched")
                .data(orderDTO)
                .build();
    }

    @Override
    public Response<List<OrderDTO>> findDeliveredOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        List<Order> orders =
                orderRepository.findDeliveredOrdersByOrderStatusAndUserId(authUser.getUser().getDeliveryPerson().getId(), OrderStatus.DELIVERED);

        List<OrderDTO> orderDTOS = orders.stream().map(order -> modelMapper.map(order,OrderDTO.class))
                .toList();

        return Response.<List<OrderDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .data(orderDTOS)
                .build();
    }

    @Override
    public Response<DashboardDTO> getDashboard(Long deliveryPersonId) {

        long assignedOrders = orderRepository.countByDeliveryPerson_IdAndOrderStatus(
                deliveryPersonId,
                OrderStatus.ASSIGNED
        );

        long completedOrders = orderRepository.countByDeliveryPerson_IdAndOrderStatus(
                deliveryPersonId,
                OrderStatus.DELIVERED
        );

        BigDecimal todayEarnings = orderRepository.sumEarningsByDeliveryPersonAndDateRange(
                deliveryPersonId,
                PaymentStatus.COMPLETED,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(23,59,59)
        );

        if (todayEarnings == null) {
            todayEarnings = BigDecimal.ZERO;
        }

        List<DailyEarningDTO> dailyEarnings = new ArrayList<>();
        for (int i = 6; i>=0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            BigDecimal earning = orderRepository.sumEarningsByDeliveryPersonAndDateRange(
                    deliveryPersonId,
                    PaymentStatus.COMPLETED,
                    day.atStartOfDay(),
                    day.atTime(23,59,59)
            );
            if (earning == null) {
                earning = BigDecimal.ZERO;
            }
            dailyEarnings.add(new DailyEarningDTO(day,earning));
        }
        DeliveryPerson dp = deliveryPersonRepository.findDeliveryPersonById(deliveryPersonId);

        if (dp == null) {
            throw new RuntimeException("Delivery person not found");
        }

        boolean isActive = dp.isHasActiveOrder();

        DashboardDTO dto =
                DashboardDTO.builder()
                        .assignedOrders(assignedOrders)
                        .completedOrders(completedOrders)
                        .todayEarnings(todayEarnings)
                        .active(isActive)
                        .dailyEarnings(dailyEarnings)
                        .build();


        return Response.<DashboardDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .data(dto)
                .build();
    }

}




















