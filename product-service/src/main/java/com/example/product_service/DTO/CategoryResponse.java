package com.example.product_service.DTO;

import com.example.product_service.Enums.CategoryStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


public record CategoryResponse(UUID categoryId, String categoryName) {}