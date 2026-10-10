package com.example.product_service.DTO;

import com.example.product_service.Enums.AttributeDataType;

import java.util.List;
import java.util.UUID;

public record CategoryAttributeDto(
        UUID id,
        String attributeName,
        AttributeDataType dataType,
        List<String> allowedValues,
        boolean isRequired
) {}
