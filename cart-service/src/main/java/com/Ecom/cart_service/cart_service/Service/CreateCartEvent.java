package com.Ecom.cart_service.cart_service.Service;

import com.Ecom.cart_service.cart_service.DTO.CartDto;
import com.Ecom.cart_service.cart_service.DTO.CartItemDto;
import com.Ecom.cart_service.cart_service.DTO.UserCreatedEvent;
import com.Ecom.cart_service.cart_service.Entity.Cart;
import com.Ecom.cart_service.cart_service.Repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CreateCartEvent {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartService cartService;

    @KafkaListener(topics = "user-created", groupId = "cart-created-service",
            containerFactory = "kafkaListenerContainerFactory" // must match your bean name
    )

    public void createCart(UserCreatedEvent event, Acknowledgment ack) {
        log.info("Going to check cart with userId{} exist or not", event.getUserId());
        try{
            if(event.getUserId()==null)
            {
                log.info("User id is null skip processing");
            }
            else{
                cartService.handleUserCartCreation(event);
            }
            ack.acknowledge();

        }
        catch(Exception e)
        {
            log.info("Excepion {} ocuured while processing event",e.getMessage());
        }

    }
}
