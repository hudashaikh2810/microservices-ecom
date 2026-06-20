package com.example.product_service.DTO;

import com.example.product_service.Enums.Category;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryDto {
    private Long productId;
    @NotEmpty
    private String productName;
    @NotEmpty
    private String productDescription;
    @NotEmpty
    private Category category;
}
