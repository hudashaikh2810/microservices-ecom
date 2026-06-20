package com.Ecom.cart_service.cart_service.Service;

import com.Ecom.cart_service.cart_service.Entity.Cart;
import com.Ecom.cart_service.cart_service.Entity.CartItem;
import com.Ecom.cart_service.cart_service.Repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CartMergeService {
    @Autowired
    private RedisService redisService;
    @Autowired
    CartRepository cartRepository;
    public Cart mergeGuestUserCart(Cart userCart, Cart guestCart) {
        for (CartItem guestItem : guestCart.getItems()) {

            Optional<CartItem> userItemOpt = userCart.getItems().stream()
                    .filter(ci -> ci.getSkuId().equals(guestItem.getSkuId()))
                    .findFirst();

            if (userItemOpt.isPresent()) {
                CartItem userItem = userItemOpt.get();
                int newQty = userItem.getQuantity() + guestItem.getQuantity();
                userItem.setQuantity(newQty);
            } else {
                userCart.getItems().add(guestItem);
            }
        }
        log.info("Saving merge cart into db");
        cartRepository.save(userCart);
        log.info("Deleting guest cart from redis if exisit");
        redisService.removeFromRedisGuestId(guestCart.getGuestId());
        log.info("Deleting guest cart from db");
        cartRepository.delete(guestCart);
        return userCart;
    }
}
