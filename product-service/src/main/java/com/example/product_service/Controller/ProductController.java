package com.example.product_service.Controller;

import com.example.product_service.DTO.ProductDto;
import com.example.product_service.DTO.ProductMetaDto;
import com.example.product_service.DTO.SKUDto;
import com.example.product_service.Entity.Product;
import com.example.product_service.Service.ProductService;
import com.example.product_service.Service.SkuCreatedEvent;
import com.example.product_service.wrapper.RestPage;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private SkuCreatedEvent evt;

    @PostMapping(value = "/admin/add")
    public ResponseEntity<?> addProdduct(@RequestBody ProductDto productDto) {
        ProductDto savedProductDto = productService.addProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProductDto);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @PutMapping(value = "/admin/update/{id}")
    public ResponseEntity<?> deleteProduct(@RequestPart("product") ProductDto productDto, @PathVariable long id) {
        ProductDto updatedProductDto = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProductDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "ELECTRONICS") String sortBy,
                                    @RequestParam(defaultValue = "asc") String sortDir) {
        RestPage<ProductMetaDto> products = productService.findAll(page, size, sortBy, sortDir);
        Map<String, Object> response = new HashMap<>();
        response.put("products", products.getContent());
        response.put("currentPage", products.getNumber());
        response.put("totalItems", products.getTotalElements());
        response.put("totalPages", products.getTotalPages());
        return ResponseEntity.ok(response);

    }


}
