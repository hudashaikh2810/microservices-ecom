package com.example.product_service.Service;

import com.example.product_service.Component.ProductMapper;
import com.example.product_service.DTO.*;
import com.example.product_service.Entity.Category;
import com.example.product_service.Entity.CategoryAttributeDefinition;

import com.example.product_service.Entity.Product;

import com.example.product_service.Entity.ProductImage;
import com.example.product_service.Enums.ProductStatus;
import com.example.product_service.Exception.*;
import com.example.product_service.Repository.CategoryAttributeDefinitionRepository;
import com.example.product_service.Repository.CategoryRepository;
import com.example.product_service.Repository.ProductImageRepository;
import com.example.product_service.Repository.ProductRepository;
import com.example.product_service.util.CursorUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class  ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryAttributeDefinitionRepository attributeDefinitionRepository;
    private final ProductImageRepository imageRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          CategoryAttributeDefinitionRepository attributeDefinitionRepository,
                          ProductMapper productMapper,
                          ProductImageRepository imageRepository)
    {
        this.productRepository=productRepository;
        this.categoryRepository=categoryRepository;
        this.attributeDefinitionRepository=attributeDefinitionRepository;
        this.productMapper=productMapper;
        this.imageRepository=imageRepository;
    }


    @Transactional
    public ProductResponse save(ProductCreateRequest request) throws CategoryNotFoundException {

        // 1. Fetch category, fail fast if it doesn't exist
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("The category doesnt exist"));

        // 2. Fetch attribute definitions for this category
        List<CategoryAttributeDefinition> definitions =
                attributeDefinitionRepository.findByCategoryId(category.getId());

        // 3. Validate incoming attributes against definitions
        validateAttributes(request.getAttributes(), definitions);

        // 4. Build entity
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setAttributes(request.getAttributes());
        product.setStatus(ProductStatus.DRAFT);   // hardcoded, not from client
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());

        Product saved = productRepository.save(product);

        return productMapper.toResponse(saved);
    }


    private void validateAttributes(Map<String, Object> attributes,
                                    List<CategoryAttributeDefinition> definitions) {

        List<String> errors = new ArrayList<>();

        Map<String, CategoryAttributeDefinition> definitionByName = definitions.stream()
                .collect(Collectors.toMap(CategoryAttributeDefinition::getAttributeName, d -> d));

        // 1. Check required attributes are present
        for (CategoryAttributeDefinition def : definitions) {
            if (def.isRequired() && !attributes.containsKey(def.getAttributeName())) {
                errors.add("Missing required attribute: '" + def.getAttributeName() + "'");
            }
        }

        // 2. Check every submitted attribute is valid for this category
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            CategoryAttributeDefinition def = definitionByName.get(key);
            if (def == null) {
                errors.add("Attribute '" + key + "' is not valid for this category");
                continue;   // no definition to validate type against, skip further checks for this key
            }

            validateValueType(key, value, def, errors);
        }

        if (!errors.isEmpty()) {
            throw new InvalidProductAttributeException(errors);
        }
    }

    private void validateValueType(String key, Object value,
                                   CategoryAttributeDefinition def, List<String> errors) {

        if (value == null) {
            errors.add("Attribute '" + key + "' cannot be null");
            return;
        }

        switch (def.getDataType()) {
            case ENUM -> {
                if (!(value instanceof String strValue) || !def.getAllowedValues().contains(strValue)) {
                    errors.add("Attribute '" + key + "' must be one of " + def.getAllowedValues()
                            + " but got '" + value + "'");
                }
            }
            case NUMBER -> {
                if (!(value instanceof Number) && !isParsableAsNumber(value)) {
                    errors.add("Attribute '" + key + "' must be a number but got '" + value + "'");
                }
            }
            case STRING -> {
                if (!(value instanceof String strValue) || strValue.isBlank()) {
                    errors.add("Attribute '" + key + "' must be a non-empty string");
                }
            }
        }
    }

    private boolean isParsableAsNumber(Object value) {
        try {
            Double.parseDouble(String.valueOf(value));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @Transactional(readOnly = true)
    public ShowProductResponse show(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not available"));

        List<ProductImage> images = imageRepository.findByProductIdOrderByDisplayOrder(id);

        return productMapper.toShowProductResponse(product, images);
    }
    @Transactional(readOnly = true)
    public ShowProductResponse showActiveProduct(UUID id) {

        Product product = productRepository.findByIdAndStatus(id, ProductStatus.ACTIVE)
                .orElseThrow(() -> new ProductNotFoundException("Product not available"));

        List<ProductImage> images = imageRepository.findByProductIdOrderByDisplayOrder(id);

        return productMapper.toShowProductResponse(product, images);
    }

    @Transactional
    public void delete(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if (product.getStatus() == ProductStatus.ARCHIVED) {
            return;
        }
        product.setStatus(ProductStatus.ARCHIVED);
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);
    }

    @Transactional
    public void update(UUID id, ProductUpdateRequest request) {

        // 1. Fetch existing product — fail fast if it doesn't exist
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // 2. Fetch category — could be same as before, or a newly chosen one
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("The category cannot be found"));

        // 3. Fetch definitions for the (possibly new) category and validate
        List<CategoryAttributeDefinition> definitions =
                attributeDefinitionRepository.findByCategoryId(category.getId());
        validateAttributes(request.getAttributes(), definitions);

        // 4. Apply changes onto the existing entity (not a new one)
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setAttributes(request.getAttributes());
        product.setUpdatedAt(Instant.now());

        productRepository.save(product);
        // @Version field auto-increments on this save; if another transaction
        // modified this row since it was fetched, this throws OptimisticLockException
    }


        public ProductPage search(UUID categoryId, String attributeKey, String attributeValue,
                                  String cursorToken, int size) {

            CursorUtil.Cursor cursor = CursorUtil.decode(cursorToken);

            List<Product> rows = productRepository.search(
                    categoryId, attributeKey, attributeValue,
                    cursor.createdAt(), cursor.id(), size + 1);

            boolean hasMore = rows.size() > size;
            List<Product> page = hasMore ? rows.subList(0, size) : rows;

            // batch-fetch primary images for this page — one query, not N+1
            List<UUID> productIds = page.stream().map(Product::getId).toList();
            Map<UUID, String> primaryImageByProductId = imageRepository
                    .findPrimaryImagesFor(productIds).stream()
                    .collect(Collectors.toMap(
                            img -> img.getProduct().getId(),
                            ProductImage::getUrl));

            List<ProductListResponse> items = page.stream()
                    .map(p -> new ProductListResponse(
                            p.getId(),
                            p.getName(),
                            p.getPrice(),
                            primaryImageByProductId.get(p.getId())))
                    .toList();

            String nextCursor = hasMore
                    ? CursorUtil.encode(page.get(page.size() - 1).getCreatedAt(), page.get(page.size() - 1).getId())
                    : null;

            return new ProductPage(items, nextCursor, hasMore);

    }
    @Transactional
    public void publish(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getStatus() == ProductStatus.ACTIVE) {
            return;   // already active — idempotent, no-op like delete()
        }

        if (product.getStatus() == ProductStatus.ARCHIVED) {
            throw new InvalidProductStateException("Cannot publish an archived product");
        }

        product.setStatus(ProductStatus.ACTIVE);
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);
    }
}