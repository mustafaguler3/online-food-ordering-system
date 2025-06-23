package com.example.FoodApp.email_notification.services.impl;

import com.example.FoodApp.email_notification.dtos.NotificationDTO;
import com.example.FoodApp.email_notification.entity.Notification;
import com.example.FoodApp.email_notification.repository.NotificationRepository;
import com.example.FoodApp.email_notification.services.NotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(NotificationDTO notificationDTO) {
        log.info("Inside sendEmail()");
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(notificationDTO.getRecipient());
            helper.setSubject(notificationDTO.getSubject());
            helper.setText(notificationDTO.getBody(),notificationDTO.isHtml());

            javaMailSender.send(mimeMessage);

            // Save to Database
            Notification notification = Notification.builder()
                    .recipient(notificationDTO.getRecipient())
                    .subject(notificationDTO.getSubject())
                    .body(notificationDTO.getBody())
                    .type(notificationDTO.getType())
                    .isHtml(notificationDTO.isHtml())
                    .build();
            notificationRepository.save(notification);
            log.info("Saved to notification table");

        }catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
























