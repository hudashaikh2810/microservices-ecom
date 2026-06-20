package com.example.product_service.DTO;

import com.example.product_service.Enums.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMetaDto {
    private Long id;
    private String name;
    private String description;
    private Category category;
    private String color;
    private String size;
    private double price;
    public String coverImageUrl;
    private boolean isAvailable;
}
