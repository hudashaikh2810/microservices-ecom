package com.example.product_service.Component;

import com.example.product_service.DTO.AttributeDefinitionResponse;
import com.example.product_service.Entity.CategoryAttributeDefinition;
import org.springframework.stereotype.Component;

@Component
public class AttributeMapper {

    public AttributeDefinitionResponse toResponse(CategoryAttributeDefinition definition) {
        return new
                AttributeDefinitionResponse(definition.getId(),definition.getAttributeName(),
                definition.getDataType(),definition.getAllowedValues(),definition.isRequired());
    }
}