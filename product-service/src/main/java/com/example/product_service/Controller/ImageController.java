package com.example.product_service.Controller;

import com.example.product_service.DTO.ImageCreateRequest;
import com.example.product_service.DTO.ImageResponse;
import com.example.product_service.DTO.ImageUpdateRequest;
import com.example.product_service.Service.ImageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products/{productId}/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity<ImageResponse> save(@PathVariable UUID productId,
                                              @Valid @RequestBody ImageCreateRequest request) {
        request.setProductId(productId);
        ImageResponse response = imageService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ImageResponse>> list(@PathVariable UUID productId) {
        return ResponseEntity.ok(imageService.list(productId));
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<Void> update(@PathVariable UUID productId,
                                       @PathVariable UUID imageId,
                                       @Valid @RequestBody ImageUpdateRequest request) {
        imageService.update(imageId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID productId,
                                       @PathVariable UUID imageId) {
        imageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }
}
