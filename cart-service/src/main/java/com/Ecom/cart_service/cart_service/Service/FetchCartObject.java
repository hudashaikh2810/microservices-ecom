package com.Ecom.cart_service.cart_service.Service;

import com.Ecom.cart_service.cart_service.Entity.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FetchCartObject {
    @Autowired
    GuestStrategy guestStrategy;
    @Autowired
    UserStartegy userStrategy;
    @Autowired
    MergeStrategy mergeStrategy;

    public Cart fetchCartObject(Long userId, Long guestId) {
        log.info("Choosing appropriate strategy to call fetch cart with userId{} and guestId{}",userId,guestId);
        if (userId != null && guestId == null) {
            log.info("Calling user strategy to fetch cart");
            return userStrategy.fetchCart(userId, null);
        }

        if (guestId != null && userId == null) {
            log.info("Calling guest strategy to fetch cart");
            return guestStrategy.fetchCart(null, guestId);
        }

        if (userId != null && guestId != null) {
            log.info("Calling merge strategy to fetch cart");
            return mergeStrategy.fetchCart(userId, guestId);
        }

        // Both are null -> no valid input
        log.warn("Both userId and guestId are null. No cart can be fetched.");
        return null;
    }

}
