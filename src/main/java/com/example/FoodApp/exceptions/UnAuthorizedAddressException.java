package com.example.FoodApp.exceptions;

public class UnAuthorizedAddressException extends RuntimeException{
    public UnAuthorizedAddressException(String message) {
        super(message);
    }
}
