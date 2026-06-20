package com.Ecom.cart_service.cart_service.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDetailDto {
    private Long id;
    private String skuId;
    private int quantity;
    private Double pricePerUnit;
    private Double totalPrice;
    private String productName;
    private String coverImage;
}
