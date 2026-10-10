package com.example.product_service.Component;

import com.example.product_service.DTO.ProductListResponse;
import com.example.product_service.DTO.ProductResponse;
import com.example.product_service.DTO.ShowProductResponse;
import com.example.product_service.Entity.Product;
import com.example.product_service.Entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {
    private final ImageMapper imageMapper;
    public ProductMapper(ImageMapper imageMapper)
    {
        this.imageMapper=imageMapper;
    }

    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        return response;
    }
    public ShowProductResponse toShowProductResponse(Product product, List<ProductImage> images) {
        ShowProductResponse response = new ShowProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setCategoryName(product.getCategory().getName());
        response.setAttributes(product.getAttributes());
        response.setStatus(product.getStatus());
        response.setImages(images.stream().map(imageMapper::toResponse).toList());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
    public ProductListResponse toListResponse(Product product, String primaryImageUrl) {
        ProductListResponse response = new ProductListResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setPrimaryImageUrl(primaryImageUrl);   // null if product has no primary image yet
        return response;
    }
}
