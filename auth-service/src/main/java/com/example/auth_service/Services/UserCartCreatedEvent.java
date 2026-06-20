package com.example.auth_service.Services;

import com.example.auth_service.DTO.MergeCartDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserCartCreatedEvent {
    @Autowired
    private KafkaTemplate<String, MergeCartDto> kafkaTemplate;
    public void createCartRequest(MergeCartDto mergeCartDto) {
        log.info("Going to send notification to cart  for User{} to merge guestCart{} if exist and create userCart",mergeCartDto.getUserId(),mergeCartDto.getGuestId()
        );
        kafkaTemplate.send("user-created", String.valueOf(mergeCartDto.getUserId()),mergeCartDto);
        log.info("Notification sent successfully");
    }
}
