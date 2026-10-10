package com.example.product_service.Controller;

import com.example.product_service.DTO.ProductResponse;
import com.example.product_service.DTO.ShowProductResponse;
import com.example.product_service.Service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowProductResponse> show(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.show(id));
    }
}
