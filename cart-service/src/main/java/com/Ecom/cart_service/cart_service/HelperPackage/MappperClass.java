package com.Ecom.cart_service.cart_service.HelperPackage;

import com.Ecom.cart_service.cart_service.DTO.CartDto;
import com.Ecom.cart_service.cart_service.DTO.CartItemDto;
import com.Ecom.cart_service.cart_service.Entity.Cart;
import com.Ecom.cart_service.cart_service.Entity.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MappperClass {
    public CartDto convertToDto(Cart cart) {
        return CartDto.builder()
                .userId(cart.getUserId())
                .lastUpdated(cart.getLastUpdated())
                .guestId(cart.getGuestId())
                .items(
                        cart.getItems().stream()
                                .map(this::convertToDto)
                                .toList()
                )
                .build();
    }

    public CartItemDto convertToDto(CartItem item) {
        return CartItemDto.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .skuId(item.getSkuId())
                .pricePerUnit(item.getPricePerUnit())
                .build();
    }


    public Cart convertToEntity(CartDto dto) {
        log.info("Going to convert from dto to entity for user {}", dto.getUserId());
        Cart cart = Cart.builder()
                .userId(dto.getUserId())
                .lastUpdated(dto.getLastUpdated())
                .guestId(dto.getGuestId())
                .build();

        cart.setItems(
                dto.getItems().stream()
                        .map(d -> convertToEntity(d, cart))
                        .toList()
        );
        log.info("Succesfully converted to entityy");
        return cart;
    }

    public CartItem convertToEntity(CartItemDto dto, Cart parentCart) {
        return CartItem.builder()
                .id(dto.getId())
                .quantity(dto.getQuantity())
                .skuId(dto.getSkuId())
                .pricePerUnit(dto.getPricePerUnit())
                .cart(parentCart)
                .build();
    }

}
