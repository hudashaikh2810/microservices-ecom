package com.example.product_service.Controller;

import com.example.product_service.DTO.CategoryCreateRequest;
import com.example.product_service.DTO.CategoryResponse;
import com.example.product_service.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> save(@Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = categoryService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list(@RequestParam(required = false) UUID parentId) {
        return ResponseEntity.ok(categoryService.list(parentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> show(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.show(id));
    }
}
