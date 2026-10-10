package com.example.product_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String primaryImageUrl;   // just the thumbnail, not full gallery


}
