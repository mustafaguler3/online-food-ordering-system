package com.example.FoodApp.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private int statusCode;
    private String message;
    private T data;
    private Map<String, Serializable> meta;
}
