package com.Ecom.cart_service.cart_service.Service;

import com.Ecom.cart_service.cart_service.DTO.MergeCartDto;
import com.Ecom.cart_service.cart_service.DTO.UserCreatedEvent;
import com.Ecom.cart_service.cart_service.Entity.Cart;
import com.Ecom.cart_service.cart_service.Repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MergeCartConsumer {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartMergeService cartMergeService;

    @KafkaListener(topics = "user-login", groupId = "cart-merge-service",
            containerFactory = "kafkaListenerContainerFactoryMergeCart" // must match your bean name
    )

    public void mergeCart(MergeCartDto event, Acknowledgment ack) {
        log.info("📥 Received MergeCart event: {}", event);

        // Minimal but safe validation
        if (event == null || event.getUserId() == null || event.getGuestId() == null) {
            log.warn("❌ Invalid merge event received: {}", event);
            ack.acknowledge();
            return;
        }

        try {
            Cart userCart = cartRepository.findByUserId(event.getUserId()).orElse(null);
            Cart guestCart = cartRepository.findByGuestId(event.getGuestId()).orElse(null);

            if (userCart != null && guestCart != null) {
                log.info("🔄 Merging guest cart into user cart. userId={}, guestId={}",
                        event.getUserId(), event.getGuestId());
                try {
                    cartMergeService.mergeGuestUserCart(userCart, guestCart);

                } catch (Exception e) {
                    log.info("Exception occured while processing req {}", e.getMessage());
                }

                log.info("✅ Merge successful for userId={}", event.getUserId());
            } else {
                log.warn("⚠ Skip merge — carts missing. userCart={}, guestCart={}",
                        userCart != null, guestCart != null);
            }

            ack.acknowledge();

        } catch (Exception e) {
            log.error("🔥 Error merging carts for event {}: {}", event, e.getMessage(), e);
            // DO NOT ACK → let Kafka retry
        }
    }

}
