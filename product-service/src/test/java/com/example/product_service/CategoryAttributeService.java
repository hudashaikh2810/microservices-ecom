package com.example.product_service;

import com.example.product_service.Component.AttributeMapper;
import com.example.product_service.DTO.AttributeDefinitionCreateRequest;
import com.example.product_service.DTO.AttributeDefinitionResponse;
import com.example.product_service.Entity.Category;
import com.example.product_service.Entity.CategoryAttributeDefinition;
import com.example.product_service.Enums.AttributeDataType;
import com.example.product_service.Exception.CategoryNotFoundException;
import com.example.product_service.Exception.InvalidAttributeDefinitionException;
import com.example.product_service.Repository.CategoryAttributeDefinitionRepository;
import com.example.product_service.Repository.CategoryRepository;
import com.example.product_service.Service.CategoryAttributeService;
import com.example.product_service.util.CacheKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class CategoryAttributeServiceImplTest {

    @Mock CategoryRepository categoryRepository;
    @Mock CategoryAttributeDefinitionRepository attributeDefinitionRepository;
    @Mock AttributeMapper attributeMapper;
    @Mock
    StringRedisTemplate redisTemplate;
    @Mock
    ValueOperations<String, String> valueOps;
    @Spy
    ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks CategoryAttributeService service;

    private final ObjectMapper testMapper = new ObjectMapper();   // test data only, never stubbed
    private static final UUID CATEGORY_ID = UUID.randomUUID();

    private static String key(UUID categoryId) {
        return CacheKeys.attributesByCategory(categoryId);
    }

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    private AttributeDefinitionResponse response(String name) {
        return new AttributeDefinitionResponse(UUID.randomUUID(), name, AttributeDataType.ENUM,
                List.of("black", "blue"), true);
    }

    private AttributeDefinitionCreateRequest request(AttributeDataType type, List<String> allowed, boolean required) {
        AttributeDefinitionCreateRequest r = new AttributeDefinitionCreateRequest();
        r.setAttributeName("color");
        r.setDataType(type);
        r.setAllowedValues(allowed);
        r.setRequired(required);
        return r;
    }

    // ---------- list ----------
    @Test
    void list_cacheHit_returnsCachedAndSkipsDb() throws Exception {
        List<AttributeDefinitionResponse> cached = List.of(response("color"), response("fit"));
        String json = testMapper.writeValueAsString(cached);   // computed before stubbing
        when(valueOps.get(key(CATEGORY_ID))).thenReturn(json);

        List<AttributeDefinitionResponse> result = service.list(CATEGORY_ID);

        assertThat(result).isEqualTo(cached);
        verifyNoInteractions(attributeDefinitionRepository);
    }

    @Test
    void list_cacheMiss_loadsFromDbAndWritesCacheWithTtl() {
        when(valueOps.get(key(CATEGORY_ID))).thenReturn(null);
        when(attributeDefinitionRepository.findByCategoryId(CATEGORY_ID))
                .thenReturn(List.of(new CategoryAttributeDefinition(), new CategoryAttributeDefinition()));
        when(attributeMapper.toResponse(any(CategoryAttributeDefinition.class))).thenReturn(response("color"));

        assertThat(service.list(CATEGORY_ID)).hasSize(2);

        verify(valueOps).set(eq(key(CATEGORY_ID)), anyString(), any(Duration.class));
    }

    @Test
    void list_cachedEmptyList_isHitNotMiss() {
        when(valueOps.get(key(CATEGORY_ID))).thenReturn("[]");

        assertThat(service.list(CATEGORY_ID)).isEmpty();
        verifyNoInteractions(attributeDefinitionRepository);
    }

    @Test
    void list_corruptCacheEntry_fallsBackToDb() {
        when(valueOps.get(key(CATEGORY_ID))).thenReturn("not-json{{");
        when(attributeDefinitionRepository.findByCategoryId(CATEGORY_ID))
                .thenReturn(List.of(new CategoryAttributeDefinition()));
        when(attributeMapper.toResponse(any(CategoryAttributeDefinition.class))).thenReturn(response("color"));

        assertThat(service.list(CATEGORY_ID)).hasSize(1);
        verify(attributeDefinitionRepository).findByCategoryId(CATEGORY_ID);
    }

    @Test
    void list_noDefinitionsInDb_returnsEmptyList() {
        when(valueOps.get(key(CATEGORY_ID))).thenReturn(null);
        when(attributeDefinitionRepository.findByCategoryId(CATEGORY_ID)).thenReturn(List.of());

        assertThat(service.list(CATEGORY_ID)).isEmpty();
    }

    // ---------- save ----------
    @Test
    void save_validEnum_persistsAllFields_andEvictsThatCategorysCache() {
        Category category = new Category();
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(attributeDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.save(CATEGORY_ID, request(AttributeDataType.ENUM, List.of("black", "blue"), true));

        ArgumentCaptor<CategoryAttributeDefinition> captor =
                ArgumentCaptor.forClass(CategoryAttributeDefinition.class);
        verify(attributeDefinitionRepository).save(captor.capture());
        CategoryAttributeDefinition saved = captor.getValue();
        assertThat(saved.getCategory()).isSameAs(category);
        assertThat(saved.getAttributeName()).isEqualTo("color");
        assertThat(saved.getDataType()).isEqualTo(AttributeDataType.ENUM);
        assertThat(saved.getAllowedValues()).containsExactly("black", "blue");
        assertThat(saved.isRequired()).isTrue();
        verify(redisTemplate).delete(key(CATEGORY_ID));   // stale list must not survive a write
    }

    @Test
    void save_enumWithNullAllowedValues_throws_andDoesNotEvict() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(new Category()));

        assertThatThrownBy(() -> service.save(CATEGORY_ID, request(AttributeDataType.ENUM, null, true)))
                .isInstanceOf(InvalidAttributeDefinitionException.class);

        verify(attributeDefinitionRepository, never()).save(any());
        verify(redisTemplate, never()).delete(anyString());   // nothing changed, so nothing to evict
    }

    @Test
    void save_enumWithEmptyAllowedValues_throws() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(new Category()));

        assertThatThrownBy(() -> service.save(CATEGORY_ID, request(AttributeDataType.ENUM, List.of(), true)))
                .isInstanceOf(InvalidAttributeDefinitionException.class);
    }

    @Test
    void save_stringTypeWithoutAllowedValues_isAllowed() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(new Category()));
        when(attributeDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.save(CATEGORY_ID, request(AttributeDataType.STRING, null, false));

        verify(attributeDefinitionRepository).save(any());
    }

    @Test
    void save_categoryNotFound_throws_andDoesNotEvict() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(CATEGORY_ID, request(AttributeDataType.STRING, null, false)))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(attributeDefinitionRepository, never()).save(any());
        verify(redisTemplate, never()).delete(anyString());
    }
}