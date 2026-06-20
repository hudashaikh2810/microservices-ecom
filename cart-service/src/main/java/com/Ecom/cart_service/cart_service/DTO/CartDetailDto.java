package com.Ecom.cart_service.cart_service.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDetailDto {
    private Long userId;
    private Long guestId;
    private List<CartItemDetailDto> items;
    private double cartTotal;
    private LocalDateTime lastUpdated;
}
