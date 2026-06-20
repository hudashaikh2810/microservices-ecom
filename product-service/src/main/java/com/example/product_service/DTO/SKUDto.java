package com.example.product_service.DTO;

import com.example.product_service.Entity.Image;
import com.example.product_service.Entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SKUDto {
    private String id;
    private String color;
    private String size;
    private double price;
    private ProductSummaryDto product;
    private List<ImageDto> images;
    private boolean isDefaultSku;
    private boolean isAvailable;

}

