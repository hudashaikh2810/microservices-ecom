package com.example.product_service.DTO;

import com.example.product_service.Enums.AttributeDataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AttributeDefinitionCreateRequest {
    @NotBlank
    private String attributeName;
    @NotNull
    private AttributeDataType dataType;
    private List<String> allowedValues;   // required only when dataType = ENUM
    private boolean required;
}