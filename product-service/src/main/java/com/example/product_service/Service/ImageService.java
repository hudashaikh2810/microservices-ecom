package com.example.product_service.Service;

import com.example.product_service.DTO.ImageCreateRequest;
import com.example.product_service.Component.ImageMapper;
import com.example.product_service.DTO.ImageResponse;
import com.example.product_service.DTO.ImageUpdateRequest;
import com.example.product_service.Entity.Product;
import com.example.product_service.Entity.ProductImage;
import com.example.product_service.Exception.ImageNotFoundException;
import com.example.product_service.Exception.InvalidImageStateException;
import com.example.product_service.Exception.ProductNotFoundException;
import com.example.product_service.Repository.ProductImageRepository;
import com.example.product_service.Repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class  ImageService {

    private final ProductImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final ImageMapper imageMapper;

    public ImageService(ProductImageRepository imageRepository,
                        ProductRepository productRepository,
                        ImageMapper imageMapper)
    {
        this.imageRepository=imageRepository;
        this.productRepository=productRepository;
        this.imageMapper=imageMapper;
    }

    @Transactional
    public ImageResponse save(ImageCreateRequest request) {

        // 1. Fail fast if product doesn't exist
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Request product doesn't exist"));

        // 2. If this image is being set as primary, unset the current primary first
        if (request.isPrimary()) {
            imageRepository.unsetPrimaryForProduct(product.getId());
        }

        // 3. Map DTO -> entity
        ProductImage image = imageMapper.toEntity(request, product);

        ProductImage saved = imageRepository.save(image);

        return imageMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID imageId) {

        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));

        boolean wasPrimary = image.isPrimary();
        UUID productId = image.getProduct().getId();

        imageRepository.delete(image);

        if (wasPrimary) {
            imageRepository.findFirstByProductIdOrderByDisplayOrder(productId)
                    .ifPresent(nextImage -> {
                        nextImage.setPrimary(true);
                        imageRepository.save(nextImage);
                    });
            // if no image remains (Optional empty), product simply has zero images — nothing to do
        }
    }

    @Transactional
    public void update(UUID imageId, ImageUpdateRequest request) {

        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));

        UUID productId = image.getProduct().getId();
        boolean currentlyPrimary = image.isPrimary();
        boolean requestedPrimary = request.isPrimary();

        // Case 0: primary status unchanged
        if (currentlyPrimary == requestedPrimary) {
            applyFields(image, request);
            imageRepository.save(image);
            return;
        }

        // Case 1: was primary, now being demoted
        if (currentlyPrimary && !requestedPrimary) {

            Optional<ProductImage> nextPrimary =
                    imageRepository.findFirstByProductIdAndIdNotOrderByDisplayOrder(productId, imageId);

            if (nextPrimary.isEmpty()) {
                throw new InvalidImageStateException(
                        "Cannot unset primary — this is the product's only image");
            }

            image.setPrimary(false);
            applyFields(image, request);
            imageRepository.save(image);

            ProductImage next = nextPrimary.get();
            next.setPrimary(true);
            imageRepository.save(next);
            return;
        }

        // Case 2: was not primary, now being promoted
        imageRepository.unsetPrimaryForProduct(productId);
        image.setPrimary(true);
        applyFields(image, request);
        imageRepository.save(image);
    }

    private void applyFields(ProductImage image, ImageUpdateRequest request) {
        image.setUrl(request.getUrl());
        image.setDisplayOrder(request.getDisplayOrder());
    }

    @Transactional(readOnly = true)
    public List<ImageResponse> list(UUID productId) {

        List<ProductImage> images = imageRepository.findByProductIdOrderByDisplayOrder(productId);

        return images.stream()
                .map(imageMapper::toResponse)
                .toList();
    }
}
