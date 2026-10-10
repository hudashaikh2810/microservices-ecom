package com.example.product_service.DTO;

import com.example.product_service.Enums.AttributeDataType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

public record AttributeDefinitionResponse(
        UUID id,
        String attributeName,
        AttributeDataType dataType,
        List<String> allowedValues,
        boolean isRequired
) {}
