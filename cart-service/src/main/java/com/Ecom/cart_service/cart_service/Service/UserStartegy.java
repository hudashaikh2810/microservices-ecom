package com.Ecom.cart_service.cart_service.Service;

import com.Ecom.cart_service.cart_service.DTO.CartDto;
import com.Ecom.cart_service.cart_service.Entity.Cart;
import com.Ecom.cart_service.cart_service.HelperPackage.MappperClass;
import com.Ecom.cart_service.cart_service.Repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Slf4j
public class UserStartegy implements FetchStrategy {

    @Autowired
    private RedisService redisService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private MappperClass mapperClass;
    @Override
    public Cart fetchCart(Long userId, Long guestId) {
        Cart cart=null;
        log.info("GuestId is null and userId is not null");
        log.info(" Going to check redis for user {}", userId);
        CartDto dto = redisService.readFromCache(userId);
        if (dto == null) {
            log.info("Cart for user {} doesn't exist in redis ,checking db", userId);
            cart = cartRepository.findByUserId(userId)
                    .orElseGet(() ->
                    {
                        log.warn("❌ No cart found for user {}", userId);
                        Cart createdCart = Cart.builder()
                                .userId(userId).lastUpdated(LocalDateTime.now())
                                .items(new ArrayList<>())
                                .build();
                        return cartRepository.save(createdCart);
                    });
        } else {
            cart = mapperClass.convertToEntity(dto);
        }
        return cart;
    }
}
