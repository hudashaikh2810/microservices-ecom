package com.example.product_service.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailDto {

        private String skuId;
        private String productName;
        private Double price;
        private String coverImageUrl;
        // getters, setters, constructors
}
