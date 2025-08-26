package com.example.FoodApp.order.services.impl;

import com.example.FoodApp.auth_users.entity.User;
import com.example.FoodApp.auth_users.services.UserService;
import com.example.FoodApp.cart.entity.Cart;
import com.example.FoodApp.cart.entity.CartItem;
import com.example.FoodApp.cart.repository.CartRepository;
import com.example.FoodApp.cart.services.CartService;
import com.example.FoodApp.config.DtoConverter;
import com.example.FoodApp.email_notification.dtos.NotificationDTO;
import com.example.FoodApp.email_notification.services.NotificationService;
import com.example.FoodApp.enums.OrderStatus;
import com.example.FoodApp.enums.PaymentStatus;
import com.example.FoodApp.exceptions.BadRequestException;
import com.example.FoodApp.exceptions.NotFoundException;
import com.example.FoodApp.menu.dtos.MenuDTO;
import com.example.FoodApp.order.dtos.OrderDTO;
import com.example.FoodApp.order.dtos.OrderItemDTO;
import com.example.FoodApp.order.entity.Order;
import com.example.FoodApp.order.entity.OrderItem;
import com.example.FoodApp.order.repository.OrderItemRepository;
import com.example.FoodApp.order.repository.OrderRepository;
import com.example.FoodApp.order.services.OrderService;
import com.example.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private final CartRepository cartRepository;
    private final DtoConverter dtoConverter;

    @Value("${base.payment.link}")
    private String basePaymentLink;

    @Override
    public Response<?> placeOrderFromCart() {
        User customer = userService.getCurrentLoggedInUser();
        String deliveryAddress = customer.getAddress();

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
                    .quantity(cartItem.getQuantity())
                    .pricePerUnit(cartItem.getPricePerUnit())
                    .subtotal(cartItem.getSubtotal())
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        Order order = Order.builder()
                .user(customer)
                .orderItems(orderItems)
                .orderDate(LocalDateTime.now())
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.INITIALIZED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        orderItems.forEach(orderItem -> orderItem.setOrder(savedOrder));
        orderItemRepository.saveAll(orderItems);

        // clear cart
        cartService.clearShoppingCart();

        OrderDTO orderDTO = dtoConverter.toOrderDto(savedOrder);

        //send email
        sendOrderConfirmationEmail(customer, orderDTO);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your order has been received! We've sent a secure payment link to your email. Please complete the payment to confirm your order.")
                .build();
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
            dto.getOrderItems().forEach(orderItemDTO -> orderItemDTO.getMenu().setReviews(null));
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
}




















