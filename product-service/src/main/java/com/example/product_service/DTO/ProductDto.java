package com.example.product_service.DTO;

import com.example.product_service.Enums.Category;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long productId;
    @NotEmpty
    private String productName;
    @NotEmpty
    private String productDescription;
    @NotEmpty
    private Category category;
    private List<SkuSummaryDto> skus;
}
