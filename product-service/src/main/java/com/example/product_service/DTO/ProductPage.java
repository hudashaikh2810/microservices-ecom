package com.example.product_service.DTO;

import com.example.product_service.Entity.Product;

import java.util.List;

public record ProductPage(List<ProductListResponse> items, String nextCursor, boolean hasMore) {}

