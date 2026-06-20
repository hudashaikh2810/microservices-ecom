package com.Ecom.cart_service.cart_service.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long userId;
    private Long guestId;
    private List<CartItemDto> items;
    private LocalDateTime lastUpdated;
}
