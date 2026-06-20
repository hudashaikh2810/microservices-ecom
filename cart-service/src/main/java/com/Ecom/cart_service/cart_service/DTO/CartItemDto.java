package com.Ecom.cart_service.cart_service.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    private String skuId;
    private int quantity;
    private double pricePerUnit;
}
