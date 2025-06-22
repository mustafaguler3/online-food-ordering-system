package com.example.FoodApp.exceptions;

import com.example.FoodApp.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleAllUnknownException(Exception ex) {
        Response<?> response = Response.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response<?>> handleNotFoundException(NotFoundException ex) {
        Response<?> response = Response.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Response<?>> handleBadRequestException(BadRequestException ex) {
        Response<?> response = Response.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<Response<?>> handlePaymentProcessingException(PaymentProcessingException ex) {
        Response<?> response = Response.builder()
                .statusCode(HttpStatus.BAD_GATEWAY.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response,HttpStatus.BAD_GATEWAY);
    }
    @ExceptionHandler(UnAuthorizedAddressException.class)
    public ResponseEntity<Response<?>> handleUnAuthorizedAddressException(UnAuthorizedAddressException ex) {
        Response<?> response = Response.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<?>> handleIllegalRequestException(IllegalArgumentException ex) {
        Response<?> response = Response.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}





















