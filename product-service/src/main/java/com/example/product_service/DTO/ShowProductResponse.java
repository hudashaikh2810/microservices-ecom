package com.example.product_service.DTO;

import com.example.product_service.Enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ShowProductResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private Map<String, Object> attributes;
    private ProductStatus status;
    private List<ImageResponse> images;   // new — only populated for show, not list
    private Instant createdAt;
}
