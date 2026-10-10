package com.example.product_service.DTO;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CategoryCreateRequest {
    @NotBlank
    private String name;
    private UUID parentId;   // nullable — null means top-level category
}
