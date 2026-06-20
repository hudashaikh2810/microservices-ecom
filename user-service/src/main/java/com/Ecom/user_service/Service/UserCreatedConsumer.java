package com.Ecom.user_service.Service;

import com.Ecom.user_service.Dto.UserCreatedEvent;
import com.Ecom.user_service.Dto.UserDto;
import com.Ecom.user_service.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserCreatedConsumer {
    @Autowired
    private UserService service;

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "user-created", groupId = "user-created-service",
    containerFactory = "kafkaListenerContainerFactory" // must match your bean name
            )
    public void createUser(UserCreatedEvent event,Acknowledgment ack) {
        log.info("Request received inside the consumer event created");
        UserDto userDto = new UserDto();
        userDto.setAuthUserId(event.getUserId());
        userDto.setEmail(event.getEmail());
        log.info("Going to call user-service to save the user email= {} and authUserId={}",userDto.getEmail(),userDto.getAuthUserId());
        if(userRepository.findByAuthUserId(event.getUserId()).isEmpty())
        {
            service.addUser(userDto);

        }
        ack.acknowledge();
    }
}
