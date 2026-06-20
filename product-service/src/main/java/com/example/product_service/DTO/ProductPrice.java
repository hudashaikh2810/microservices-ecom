package com.example.product_service.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductPrice {
    private String skuId;
    private Double price;
}
