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
public class GuestStrategy implements FetchStrategy{

    @Autowired
    private RedisService redisService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    MappperClass mapperClass;
    @Override
    public Cart fetchCart(Long userId, Long guestId) {
        Cart cart=null;
        log.info("Guest id is not null & userId is null");
        log.info("Going to check whether cart with guest-id{} exist or not in cache", guestId);
        CartDto dto = redisService.readFromCacheByGuestId(guestId);
        if (dto != null) {
            log.info("Cart of guest {} exist in redis", guestId);
            cart = mapperClass.convertToEntity(dto);
        } else {
            cart = cartRepository.findByGuestId(guestId).orElseGet(()->{
                log.info("Cart with guest-id {} doesnt exist in db, going to create cart for user with guestId {}", guestId, guestId);
              Cart createdCart = Cart.builder().guestId(guestId).lastUpdated(LocalDateTime.now()).items(new ArrayList<>())
                      .build();
                return cartRepository.save(createdCart);
            });



        }
        return cart;
    }
}
