package com.example.auth_service.Services;

import com.example.auth_service.DTO.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthEventProducer {
    @Autowired
    private KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        log.info("Going to send notification to user-service for User{}", event.getEmail());
        kafkaTemplate.send("user-created", String.valueOf(event.getUserId()), event);
        log.info("Notification sent successfully");
    }






}
