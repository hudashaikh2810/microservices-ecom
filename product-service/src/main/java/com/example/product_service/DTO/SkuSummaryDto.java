package com.example.product_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkuSummaryDto {
    private String id;
    private String color;
    private String size;
    private double price;
    private List<ImageDto> images;
    private boolean isDefaultSku;
    private boolean isAvailable;

}
