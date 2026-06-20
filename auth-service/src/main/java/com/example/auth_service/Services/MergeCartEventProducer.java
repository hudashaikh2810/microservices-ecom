package com.example.auth_service.Services;

import com.example.auth_service.DTO.MergeCartDto;
import com.example.auth_service.DTO.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MergeCartEventProducer {
    @Autowired
    private KafkaTemplate<String, MergeCartDto> kafkaTemplate;

    public void sendMergeCartRequest(MergeCartDto mergeCartDto) {
        log.info("Going to send notification to cart  for User{} to merge guestCart{}",mergeCartDto.getUserId(),mergeCartDto.getGuestId()
        );
        kafkaTemplate.send("user-login", String.valueOf(mergeCartDto.getUserId()),mergeCartDto);
        log.info("Notification sent successfully");
    }



}
