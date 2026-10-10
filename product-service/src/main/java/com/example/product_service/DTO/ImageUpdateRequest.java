package com.example.product_service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageUpdateRequest {
    @NotBlank
    private String url;
    @NotNull
    private Integer displayOrder;
    private boolean isPrimary;
}
