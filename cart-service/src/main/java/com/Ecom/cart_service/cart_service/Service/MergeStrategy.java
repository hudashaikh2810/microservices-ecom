package com.Ecom.cart_service.cart_service.Service;

import com.Ecom.cart_service.cart_service.DTO.CartDto;
import com.Ecom.cart_service.cart_service.Entity.Cart;
import com.Ecom.cart_service.cart_service.Entity.CartItem;
import com.Ecom.cart_service.cart_service.HelperPackage.MappperClass;
import com.Ecom.cart_service.cart_service.Repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class MergeStrategy implements FetchStrategy{

    @Autowired
    private RedisService redisService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private MappperClass mapperClass;
    @Autowired
    private CartMergeService cartMergeService;
    @Override
    public Cart fetchCart(Long userId, Long guestId) {
        Cart guestCart = null;
        Cart cart=null;
        log.info("Going to check guest cart {} in redis", guestId);
        CartDto dto = redisService.readFromCacheByGuestId(guestId);
        if (dto == null) {
            guestCart = cartRepository.findByGuestId(guestId).orElse(null);

        } else {
            guestCart = mapperClass.convertToEntity(dto);
        }
        log.info("Going to check user cart {} in db", userId);
        dto = redisService.readFromCache(userId);
        if (dto == null) {
            cart = cartRepository.findByUserId(guestId).orElse(null);

        } else {
            cart = mapperClass.convertToEntity(dto);
        }
        log.info("Going to merge guest cart & user cart");
        if (guestCart != null && cart != null) {
            cart = cartMergeService.mergeGuestUserCart(guestCart, cart);
        }
    return cart;
    }


}
