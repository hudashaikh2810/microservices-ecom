package com.example.product_service.Service;

import com.example.product_service.Component.AttributeMapper;
import com.example.product_service.Component.CategoryMapper;
import com.example.product_service.DTO.AttributeDefinitionCreateRequest;
import com.example.product_service.DTO.AttributeDefinitionResponse;
import com.example.product_service.DTO.CategoryResponse;
import com.example.product_service.Entity.Category;
import com.example.product_service.Entity.CategoryAttributeDefinition;
import com.example.product_service.Enums.AttributeDataType;
import com.example.product_service.Exception.CategoryNotFoundException;
import com.example.product_service.Exception.InvalidAttributeDefinitionException;
import com.example.product_service.Repository.CategoryAttributeDefinitionRepository;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryAttributeService {
    private final CategoryAttributeDefinitionRepository attributeDefinitionRepository;
    private final AttributeMapper attributeMapper;
    private final CategoryRepository categoryRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;


    @Transactional
    public AttributeDefinitionResponse save(UUID categoryId, AttributeDefinitionCreateRequest request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("categoryId"));

        if (request.getDataType() == AttributeDataType.ENUM
                && (request.getAllowedValues() == null || request.getAllowedValues().isEmpty())) {
            throw new InvalidAttributeDefinitionException("allowedValues is required for ENUM type attributes");
        }

        CategoryAttributeDefinition definition = new CategoryAttributeDefinition();
        definition.setCategory(category);
        definition.setAttributeName(request.getAttributeName());
        definition.setDataType(request.getDataType());
        definition.setAllowedValues(request.getAllowedValues());
        definition.setRequired(request.isRequired());

        CategoryAttributeDefinition saved = attributeDefinitionRepository.save(definition);

        evictAttributeDefinitions(categoryId);

        return attributeMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AttributeDefinitionResponse> list(UUID categoryId) {
        String key = CacheKeys.attributesByCategory(categoryId);

        List<AttributeDefinitionResponse> cached = readAttributeList(key);
        if (cached != null) {
            return cached;
        }

        List<AttributeDefinitionResponse> fresh = attributeDefinitionRepository.findByCategoryId(categoryId).stream()
                .map(attributeMapper::toResponse)
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

    @SuppressWarnings("unchecked")
    private List<AttributeDefinitionResponse> readAttributeList(String key) {
        String json = stringRedisTemplate.opsForValue().get(key);

        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<AttributeDefinitionResponse>>() {}
            );
        } catch (JsonProcessingException e) {
            stringRedisTemplate.delete(key);   // drop the bad entry
            return null;
        }
    }

    public void evictAttributeDefinitions(UUID categoryId) {
        stringRedisTemplate.delete(
                CacheKeys.attributesByCategory(categoryId));
    }
}
