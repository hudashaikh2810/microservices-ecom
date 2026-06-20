package com.Ecom.cart_service.cart_service.Repository;

import com.Ecom.cart_service.cart_service.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.userId = :userId")
    Optional<Cart> findByUserId(Long userId);
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.guestId = :guestId")
    Optional<Cart> findByGuestId(Long guestId);
}
