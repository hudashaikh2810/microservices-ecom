package com.example.product_service.util;

import java.time.Duration;
import java.util.UUID;

public final class CacheKeys {
    private CacheKeys() {}

    public static final String CATEGORY_ROOT = "category:root";
    public static final Duration TTL = Duration.ofHours(24);

    public static String categoryByParent(UUID parentId) {
        return "category:" + parentId;
    }

    public static String attributesByCategory(UUID categoryId) {
        return "categoryAttribute:" + categoryId;
    }
}
