package com.example.product_service.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ImageCreateRequest {

    @NotNull
    private UUID productId;

    @NotBlank
    private String url;

    @NotNull
    private Integer displayOrder;

    private boolean primary;
}
