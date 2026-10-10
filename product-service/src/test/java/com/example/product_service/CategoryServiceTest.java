package com.example.product_service;

import com.example.product_service.Component.CategoryMapper;
import com.example.product_service.DTO.CategoryCreateRequest;
import com.example.product_service.DTO.CategoryResponse;
import com.example.product_service.Entity.Category;
import com.example.product_service.Enums.CategoryStatus;
import com.example.product_service.Exception.CategoryNotFoundException;
import com.example.product_service.Repository.CategoryRepository;
import com.example.product_service.Service.CategoryService;
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
class CategoryServiceImplTest {

    private static final String ROOT_KEY = "category:root";

    @Mock
    CategoryRepository categoryRepository;
    @Mock
    CategoryMapper categoryMapper;
    @Mock
    StringRedisTemplate redisTemplate;
    @Mock
    ValueOperations<String, String> valueOps;
    @Spy
    ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    CategoryService service;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    private CategoryResponse response(String name) {
        return new CategoryResponse(UUID.randomUUID(), name);
    }

    // ---------- list: root categories ----------
    @Test
    void list_cacheHit_returnsCachedAndSkipsDb() throws Exception {
        List<CategoryResponse> cached = List.of(response("Men"), response("Women"));
        String json = objectMapper.writeValueAsString(cached);   // computed first, outside the stubbing
        when(valueOps.get(ROOT_KEY)).thenReturn(json);

        List<CategoryResponse> result = service.list(null);

        assertThat(result).isEqualTo(cached);
        verifyNoInteractions(categoryRepository);
    }
    @Test
    void list_cacheMiss_loadsFromDbAndWritesCacheWithTtl() {
        when(valueOps.get(ROOT_KEY)).thenReturn(null);
        when(categoryRepository.findByParentIsNull()).thenReturn(List.of(new Category()));
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(response("Men"));

        List<CategoryResponse> result = service.list(null);

        assertThat(result).hasSize(1);
        verify(valueOps).set(eq(ROOT_KEY), anyString(), any(Duration.class));
    }

    @Test
    void list_cachedEmptyList_isTreatedAsHitNotMiss() {
        when(valueOps.get(ROOT_KEY)).thenReturn("[]");

        assertThat(service.list(null)).isEmpty();
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void list_corruptCacheEntry_fallsBackToDb() {
        when(valueOps.get(ROOT_KEY)).thenReturn("not-json{{");
        when(categoryRepository.findByParentIsNull()).thenReturn(List.of(new Category()));
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(response("Men"));

        assertThat(service.list(null)).hasSize(1);
        verify(categoryRepository).findByParentIsNull();
    }

    // ---------- list: subcategories ----------
    @Test
    void list_withParent_cacheMiss_queriesByParentId() {
        UUID parentId = UUID.randomUUID();
        when(valueOps.get(anyString())).thenReturn(null);
        when(categoryRepository.findByParentId(parentId)).thenReturn(List.of(new Category(), new Category()));
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(response("Jeans"));

        assertThat(service.list(parentId)).hasSize(2);
        verify(categoryRepository, never()).findByParentIsNull();
    }

    // ---------- save ----------
    @Test
    void save_topLevel_hasNoParent_isActive_andEvictsRootCache() {
        CategoryCreateRequest req = new CategoryCreateRequest();
        req.setName("Bottomwear");
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        service.save(req);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertThat(captor.getValue().getParent()).isNull();
        assertThat(captor.getValue().getStatus()).isEqualTo(CategoryStatus.ACTIVE);
        verify(categoryRepository, never()).findById(any());
        verify(redisTemplate).delete(ROOT_KEY);
    }

    @Test
    void save_withParent_linksParent_andEvictsCache() {
        UUID parentId = UUID.randomUUID();
        Category parent = new Category();
        parent.setId(parentId);
        CategoryCreateRequest req = new CategoryCreateRequest();
        req.setName("Jeans");
        req.setParentId(parentId);
        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        service.save(req);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertThat(captor.getValue().getParent()).isSameAs(parent);
        verify(redisTemplate, atLeastOnce()).delete(anyString());
    }

    @Test
    void save_parentNotFound_throwsAndNeverSaves() {
        UUID parentId = UUID.randomUUID();
        CategoryCreateRequest req = new CategoryCreateRequest();
        req.setName("Jeans");
        req.setParentId(parentId);
        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(req)).isInstanceOf(CategoryNotFoundException.class);
        verify(categoryRepository, never()).save(any());
        verify(redisTemplate, never()).delete(anyString());   // no eviction on failure
    }

    // ---------- show ----------
    @Test
    void show_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.show(id)).isInstanceOf(CategoryNotFoundException.class);
    }
}
