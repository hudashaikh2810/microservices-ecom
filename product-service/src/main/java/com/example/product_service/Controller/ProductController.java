package com.example.product_service.Controller;

import com.example.product_service.DTO.*;
import com.example.product_service.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> save(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.save(request);

        return ResponseEntity.created(URI.create("/products/" + response.getId())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowProductResponse> show(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.showActiveProduct(id));
    }

    @GetMapping
    public ResponseEntity<ProductPage> search(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String attributeKey,
            @RequestParam(required = false) String attributeValue,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                productService.search(categoryId, attributeKey, attributeValue, cursor, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id,
                                       @Valid @RequestBody ProductUpdateRequest request) {
        productService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<Void> publish(@PathVariable UUID id) {
        productService.publish(id);
        return ResponseEntity.noContent().build();
    }
}