package com.example.product_service.Component;

import com.example.product_service.DTO.ImageCreateRequest;
import com.example.product_service.DTO.ImageResponse;
import com.example.product_service.Entity.Product;
import com.example.product_service.Entity.ProductImage;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ImageMapper {

    public ProductImage toEntity(ImageCreateRequest request, Product product) {
        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setUrl(request.getUrl());
        image.setDisplayOrder(request.getDisplayOrder());
        image.setPrimary(request.isPrimary());
        image.setCreatedAt(Instant.now());
        return image;
    }

    public ImageResponse toResponse(ProductImage image) {
        ImageResponse response = new ImageResponse();
        response.setId(image.getId());
        response.setUrl(image.getUrl());
        response.setDisplayOrder(image.getDisplayOrder());
        response.setPrimary(image.isPrimary());
        return response;
    }
}
