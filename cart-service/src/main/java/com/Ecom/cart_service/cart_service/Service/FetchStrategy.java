package com.Ecom.cart_service.cart_service.Service;

import com.Ecom.cart_service.cart_service.Entity.Cart;

public interface FetchStrategy {
    public Cart fetchCart(Long userId, Long guestId);
}
