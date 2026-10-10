package com.example.product_service;

import com.example.product_service.DTO.CategoryCreateRequest;
import com.example.product_service.DTO.CategoryResponse;
import com.example.product_service.Entity.Category;
import com.example.product_service.Enums.CategoryStatus;
import com.example.product_service.Exception.CategoryNotFoundException;
import com.example.product_service.Repository.CategoryRepository;
import com.example.product_service.Service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Spring Boot / Spring test
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

// HTTP: this is where MediaType comes from
import org.springframework.http.MediaType;

// MockMvc static helpers: post(), get(), status(), jsonPath()
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Redis / JDBC / DB exceptions
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

// AssertJ
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Java
import java.util.List;
import java.util.UUID;

class CategoryServiceIT extends AbstractIntegrationTest {

    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    MockMvc mockMvc;

    // use your real key builder, e.g. CacheKeys.rootCategories()
    private static final String ROOT_KEY = "category:root";

    @BeforeEach
    void cleanState() {
        jdbcTemplate.execute("TRUNCATE TABLE category CASCADE");   // CASCADE: the self-FK and child tables
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    private CategoryCreateRequest request(String name, UUID parentId) {
        CategoryCreateRequest r = new CategoryCreateRequest();
        r.setName(name);
        r.setParentId(parentId);
        return r;
    }

    // ---------- Postgres ----------
    @Test
    void save_topLevelCategory_isPersistedAsActiveWithNoParent() {
        categoryService.save(request("Bottomwear", null));

        List<Category> rows = categoryRepository.findByParentIsNull();
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getName()).isEqualTo("Bottomwear");
        assertThat(rows.get(0).getStatus()).isEqualTo(CategoryStatus.ACTIVE);
    }

    @Test
    void save_subcategory_isLinkedToParent_andFoundByParentId() {
        CategoryResponse parent = categoryService.save(request("Bottomwear", null));

        categoryService.save(request("Jeans", parent.categoryId()));

        assertThat(categoryRepository.findByParentId(parent.categoryId()))
                .extracting(Category::getName).containsExactly("Jeans");
    }

    @Test
    void save_sameNameUnderSameParent_violatesUniqueConstraint() {
        categoryService.save(request("Bottomwear", null));
        CategoryResponse parent = categoryService.save(request("Footwear", null));
        categoryService.save(request("Casual", parent.categoryId()));

        assertThatThrownBy(() -> categoryService.save(request("Casual", parent.categoryId())))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_unknownParent_throwsCategoryNotFound() {
        assertThatThrownBy(() -> categoryService.save(request("Jeans", UUID.randomUUID())))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    // ---------- Redis: read path ----------
    @Test
    void list_firstCallPopulatesRedisWithTtl_asPlainJson() {
        categoryService.save(request("Men", null));
        categoryService.save(request("Women", null));
        redisTemplate.delete(ROOT_KEY);   // save evicts anyway; make the starting state explicit

        categoryService.list(null);

        String json = redisTemplate.opsForValue().get(ROOT_KEY);
        assertThat(json).isNotNull()
                .contains("Men", "Women")
                .doesNotContain("@class");            // StringRedisTemplate + ObjectMapper, no type info
        assertThat(redisTemplate.getExpire(ROOT_KEY)).isPositive();   // TTL is actually set
    }

    @Test
    void list_secondCallIsServedFromRedis_notFromPostgres() {
        categoryService.save(request("Men", null));
        categoryService.list(null);   // warm the cache

        // wipe the DB behind the service's back; only the cache still knows about "Men"
        jdbcTemplate.execute("TRUNCATE TABLE category CASCADE");

        List<CategoryResponse> result = categoryService.list(null);

        assertThat(result).extracting(CategoryResponse::categoryName).containsExactly("Men");
    }

    @Test
    void list_cachedValueDeserializesToRealRecords() {
        categoryService.save(request("Men", null));
        List<CategoryResponse> fromDb = categoryService.list(null);   // miss: DB then cache

        List<CategoryResponse> fromCache = categoryService.list(null);   // hit: Redis then JSON

        assertThat(fromCache).isEqualTo(fromDb);   // the exact bug you had: this used to blow up on the hit
    }

    @Test
    void list_corruptCacheEntry_fallsBackToPostgres() {
        categoryService.save(request("Men", null));
        redisTemplate.opsForValue().set(ROOT_KEY, "not-json{{");

        assertThat(categoryService.list(null))
                .extracting(CategoryResponse::categoryName).containsExactly("Men");
    }

    // ---------- Redis: write path / eviction ----------
    @Test
    void save_evictsRootCache_soNextListIsNotStale() {
        categoryService.save(request("Men", null));
        assertThat(categoryService.list(null)).hasSize(1);   // cached with 1 item

        categoryService.save(request("Women", null));        // must evict

        assertThat(redisTemplate.hasKey(ROOT_KEY)).isFalse();
        assertThat(categoryService.list(null)).hasSize(2);   // not the stale 1
    }

    @Test
    void save_failedSave_doesNotEvictCache() {
        categoryService.save(request("Men", null));
        categoryService.list(null);   // warm

        assertThatThrownBy(() -> categoryService.save(request("Jeans", UUID.randomUUID())))
                .isInstanceOf(CategoryNotFoundException.class);

        assertThat(redisTemplate.hasKey(ROOT_KEY)).isTrue();   // nothing changed, cache stays
    }

    // ---------- full HTTP stack ----------
    @Test
    void http_createThenList_returnsCategoryAsJson() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Bottomwear\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("Bottomwear"));
    }

    @Test
    void http_unknownCategory_returns404WithErrorBody() throws Exception {
        mockMvc.perform(get("/categories/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
