package com.example.product_service.Service;

import com.example.product_service.DTO.ImageDto;
import com.example.product_service.DTO.ProductDetailDto;
import com.example.product_service.DTO.ProductPrice;
import com.example.product_service.DTO.SKUDto;
import com.example.product_service.Entity.Image;
import com.example.product_service.Entity.Product;
import com.example.product_service.Entity.Sku;
import com.example.product_service.Exception.ProductWithIdNotFound;
import com.example.product_service.Exception.SkuWithIdNotFound;
import com.example.product_service.Mapper.ImageMapper;
import com.example.product_service.Mapper.SkuMapper;
import com.example.product_service.Repository.ProductRepository;
import com.example.product_service.Repository.SkuRepository;
import jakarta.persistence.PrePersist;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SkuService {
    @Autowired
    private SkuRepository skuRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private ImageService imageService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ImageMapper imageMapper;

    @Transactional
    public SKUDto createSku(Long productId, SKUDto skuDto, List<MultipartFile> file, List<ImageDto> imageDto) {
        Sku sku = skuMapper.convertToEntity(skuDto);
        List<Image> imagesSaved = saveImage(file);
        List<Image> images = imageDto.stream().map(dto ->
        {
            String imageUrl = UUID.randomUUID() + "-" + dto.getUrl();
            dto.setUrl(imageUrl);
            return imageMapper.convertToDTO(dto);
        }).collect(Collectors.toList());
        if (sku.isDefaultSku()) {
            skuRepository.findDefaultSkuByProductId(productId).ifPresent(isSkuDefault -> isSkuDefault.setDefaultSku(false));

        }
        sku.setImages(images);
        Product p = productRepository.findById(productId).orElseThrow(() -> new ProductWithIdNotFound("Product cannot be found"));
        p.getSkus().add(sku);
        sku.setId(generateId(p, sku));
        sku.setProduct(p);
        images.forEach(image -> image.setSku(sku));
        p = productRepository.save(p);
        System.out.println("id=" + sku.getId() + " product=" + sku.getProduct().getProductId());
        return skuMapper.convertToDTo(sku);

    }

    @Transactional
    public SKUDto updateSku(String skuId, SKUDto skuDto, List<MultipartFile> file, List<ImageDto> imageDto) {
        Sku savedSku = skuRepository.findById(skuId).orElseThrow(() -> new SkuWithIdNotFound("Sku not present"));
        Sku update = skuMapper.convertToEntity(skuDto);
        imageService.deleteFiles(savedSku.getImages());
        List<Image> imagesSaved = saveImage(file);
        List<Image> images = imageDto.stream().map(dto ->
        {
            String imageUrl = UUID.randomUUID() + "-" + dto.getUrl();
            dto.setUrl(imageUrl);
            return imageMapper.convertToDTO(dto);
        }).toList();
        savedSku.getImages().clear();
        savedSku.getImages().addAll(images);
        savedSku.setProduct(savedSku.getProduct());
        if (update.isDefaultSku()) {
            skuRepository.findDefaultSkuByProductId(savedSku.getProduct().getProductId()).ifPresent(isSkuDefault -> isSkuDefault.setDefaultSku(false));

        }
        savedSku.setColor(update.getColor());
        savedSku.setSize(update.getSize());
        savedSku.setPrice(update.getPrice());
        savedSku.setDefaultSku(update.isDefaultSku());
        savedSku.setAvailable(update.isAvailable());
        images.forEach(image -> image.setSku(savedSku));
        redisService.deleteFromRedis(redisService.getKeyForProduct(savedSku.getProduct().getProductId()));
        return skuMapper.convertToDTo(savedSku);
    }

    public SKUDto getSku(String skuId) {
        Sku savedSku = skuRepository.findById(skuId).orElseThrow(() -> new SkuWithIdNotFound("SKU not found"));
        return skuMapper.convertToDTo(savedSku);
    }

    public void deleteSku(String skuId) {
        Sku savedSku = skuRepository.findById(skuId).orElseThrow(() -> new SkuWithIdNotFound("SKU not found"));
        skuRepository.delete(savedSku);
        redisService.deleteFromRedis(redisService.getKeyForProduct(savedSku.getProduct().getProductId()));

    }

    public String generateId(Product product, Sku sku) {
        String randomCode = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        // Final SKU format
        return "SKU-" + product.getProductId() + "-" + sku.getColor() + "-" + sku.getSize() + "-" + randomCode;
    }


    private List<Image> saveImage(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        return files.stream()
                .map(file -> {
                    try {
                        String fileName = imageService.save(file);
                        Image image = new Image();
                        image.setUrl(fileName);
                        return image;
                    } catch (IOException e) {
                        log.error("Failed to save image: {}", file.getOriginalFilename(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

    }
    public Double getSkuPrice(String skuId)
    {
        Sku sku= skuRepository.findById(skuId).orElseThrow(()->new SkuWithIdNotFound("Sky with this id doesnt exist"));
        return sku.getPrice();
    }
    public List<ProductPrice> getSkuPrice(List<String> skuIds) {
        // Single database query for all SKUs
        List<Sku> skus = skuRepository.findAllById(skuIds);

        // Create a map for O(1) lookup
        Map<String, Sku> skuMap = skus.stream()
                .collect(Collectors.toMap(Sku::getId, Function.identity()));

        // Build response and validate
        return skuIds.stream()
                .map(skuId -> {
                    Sku sku = skuMap.get(skuId);
                    if (sku == null) {
                        throw new SkuWithIdNotFound("Sku with id not found: " + skuId);
                    }
                    return ProductPrice.builder()
                            .price(sku.getPrice())
                            .skuId(skuId)
                            .build();
                })
                .toList();
    }

    public ProductDetailDto getSkuDetail(String id) {
        Sku sku = skuRepository.findById(id)
                .orElseThrow(() -> new SkuWithIdNotFound("Sku with id " + id + " does not exist"));

        String coverImageUrl = sku.getImages().stream()
                .filter(Image::isPrimary)
                .findFirst()
                .map(Image::getUrl)
                .orElse(null); // or throw exception if image is mandatory

        return ProductDetailDto.builder()
                .price(sku.getPrice())
                .productName(sku.getProduct().getProductName())
                .coverImageUrl(coverImageUrl)
                .build();
    }
}
