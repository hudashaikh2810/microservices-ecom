package com.example.product_service.Component;

import com.example.product_service.DTO.CategoryResponse;
import com.example.product_service.Entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(Category category) {

        return new CategoryResponse(category.getId(),category.getName());
    }
}