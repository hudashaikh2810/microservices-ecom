package com.example.product_service.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SKUMetaDto {
    private String color;
    private String size;
    private double price;
    private ProductMetaDto product;
    public String coverImageUrl;
    private boolean isAvailable;
}
