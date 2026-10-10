package com.example.product_service.Controller;

import com.example.product_service.DTO.AttributeDefinitionCreateRequest;
import com.example.product_service.DTO.AttributeDefinitionResponse;
import com.example.product_service.Service.CategoryAttributeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories/{categoryId}/attributes")
public class CategoryAttributeController {

    private final CategoryAttributeService attributeService;

    public CategoryAttributeController(CategoryAttributeService attributeService)
    {
        this.attributeService=attributeService;
    }

    @PostMapping
    public ResponseEntity<AttributeDefinitionResponse> save(
            @PathVariable UUID categoryId,
            @Valid @RequestBody AttributeDefinitionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attributeService.save(categoryId, request));
    }

    @GetMapping
    public ResponseEntity<List<AttributeDefinitionResponse>> list(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(attributeService.list(categoryId));
    }
}