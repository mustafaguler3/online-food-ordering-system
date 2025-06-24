package com.example.FoodApp.email_notification.dtos;

import com.example.FoodApp.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class NotificationDTO {

    private long id;
    private String subject;
    @NotBlank(message = "recipient is required")
    private String recipient;
    private String body;
    private NotificationType type;
    private final LocalDateTime createdAt;
    private boolean isHtml;

}




















