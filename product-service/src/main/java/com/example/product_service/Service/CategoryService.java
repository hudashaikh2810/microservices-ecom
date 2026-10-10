package com.example.product_service.Service;

import com.example.product_service.Component.CategoryMapper;
import com.example.product_service.DTO.CategoryCreateRequest;
import com.example.product_service.DTO.CategoryResponse;
import com.example.product_service.Entity.Category;
import com.example.product_service.Enums.CategoryStatus;
import com.example.product_service.Exception.CategoryNotFoundException;
import com.example.product_service.Repository.CategoryRepository;
import com.example.product_service.util.CacheKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class  CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;


    @Transactional
    public CategoryResponse save(CategoryCreateRequest request) {

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setParent(parent);
        category.setStatus(CategoryStatus.ACTIVE);
        category.setCreatedAt(Instant.now());

        Category saved = categoryRepository.save(category);

        if (request.getParentId() == null) {
            evictRootCategories();
        } else {
            evictSubcategories(request.getParentId());
        }

        return categoryMapper.toResponse(saved);
    }


    @Transactional(readOnly = true)
    public CategoryResponse show(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        return categoryMapper.toResponse(category);
    }
    @Transactional(readOnly = true)
    public List<CategoryResponse> list(UUID parentId) {
        String key = (parentId == null)
                ? CacheKeys.CATEGORY_ROOT
                : CacheKeys.categoryByParent(parentId);

        List<CategoryResponse> cached = readList(key);
        if (cached != null) {
            return cached;
        }

        List<Category> categories = (parentId == null)
                ? categoryRepository.findByParentIsNull()
                : categoryRepository.findByParentId(parentId);

        List<CategoryResponse> fresh = categories.stream()
                .map(categoryMapper::toResponse)
                .toList();


        try {
            String json = objectMapper.writeValueAsString(fresh);
            stringRedisTemplate.opsForValue().set(key, json, CacheKeys.TTL);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to serialize category cache: " + key, e
            );
        }


        return fresh;
    }

    private List<CategoryResponse> readList(String key) {
        String json = stringRedisTemplate.opsForValue().get(key);

        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<CategoryResponse>>() {}
            );
        } catch (JsonProcessingException e) {
            stringRedisTemplate.delete(key);   // drop the bad entry
            return null;
        }
    }

    public void evictRootCategories() {
        stringRedisTemplate.delete(CacheKeys.CATEGORY_ROOT);
    }

    public void evictSubcategories(UUID parentId) {
        stringRedisTemplate.delete(CacheKeys.categoryByParent(parentId));
    }
}
