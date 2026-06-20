package com.Ecom.cart_service.cart_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
    private Long userId;
    private Long guestId;
    private List<CartItemDetailDto> items;
    private double cartTotal;
    private LocalDateTime lastUpdated;
}
