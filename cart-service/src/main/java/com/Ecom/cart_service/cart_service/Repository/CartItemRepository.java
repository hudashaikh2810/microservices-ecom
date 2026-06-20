package com.Ecom.cart_service.cart_service.Repository;

import com.Ecom.cart_service.cart_service.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
}
