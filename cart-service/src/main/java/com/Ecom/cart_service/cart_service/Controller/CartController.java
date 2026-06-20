package com.Ecom.cart_service.cart_service.Controller;

import com.Ecom.cart_service.cart_service.DTO.CartDetailDto;
import com.Ecom.cart_service.cart_service.DTO.CartDto;
import com.Ecom.cart_service.cart_service.DTO.CartItemDto;
import com.Ecom.cart_service.cart_service.Exception.CartNotFoundException;
import com.Ecom.cart_service.cart_service.Service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
@Autowired
    private  CartService cartService;

    // ---------------------------------------------
    // GET CART
    // ---------------------------------------------
    @GetMapping
    public ResponseEntity<CartDetailDto> getCart(@CookieValue(value="guestId",required=false) Long guestId,@RequestHeader(value="X-User-Id") Long userId) {
        log.info("GET /api/cart for user={}", userId);
        CartDetailDto cart = cartService.getCartByUser(userId,guestId);
        return ResponseEntity.ok(cart);
    }
    @PostMapping
    public ResponseEntity<CartDto> createCart(@RequestBody CartDto cartDto,@CookieValue(value="guestId",required=false) Long guestId)
    {
        log.info("Going to create cart");
        cartDto.setGuestId(guestId);
        cartDto.setLastUpdated(LocalDateTime.now());
        cartDto.setItems(new ArrayList<>());
        CartDto cart=cartService.createCart(cartDto);
        return ResponseEntity.ok(cart);
    }

    // ---------------------------------------------
    // ADD ITEM TO CART
    // ---------------------------------------------
    @PostMapping("/item")
    public ResponseEntity<CartDto> addItem(@RequestHeader(value="X-User-Id",required=false) Long userId,
                                           @CookieValue(value="guestId",required=false) Long guestId,
                                           @RequestBody CartItemDto itemDto) throws ServiceUnavailableException {
        log.info("POST /api/cart/item for user={} item={},guestId{}", userId, itemDto,guestId);
        CartDto updated = cartService.addItem(guestId,userId, itemDto);
        return ResponseEntity.ok(updated);
    }

    // ---------------------------------------------
    // REMOVE ITEM FROM CART
    // ---------------------------------------------
    @DeleteMapping("/item/{userId}")
    public ResponseEntity<CartDto> removeItem(@RequestHeader(value="X-User-Id",required=false) Long userId,
                                           @CookieValue(value="guestId",required=false) Long guestId,
                                           @RequestBody CartItemDto itemDto)  {
        log.info("POST /api/cart/item for user={} item={},guestId{}", userId, itemDto,guestId);
        CartDto updated = cartService.removeItem(guestId,userId, itemDto);
        return ResponseEntity.ok(updated);
    }
}
