package com.order_service.order_servie.DTO;

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
