package com.example.FoodApp.email_notification.entity;

import com.example.FoodApp.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notifications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String subject;
    @NotBlank(message = "recipient is required")
    private String recipient;
    @Lob
    private String body;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private boolean isHtml;
}





















