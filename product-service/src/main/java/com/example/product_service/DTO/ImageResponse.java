package com.example.product_service.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ImageResponse {
    private UUID id;
    private String url;
    private Integer displayOrder;
    private boolean primary;
}
