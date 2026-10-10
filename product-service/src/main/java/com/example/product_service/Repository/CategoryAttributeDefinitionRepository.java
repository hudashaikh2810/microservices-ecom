package com.example.product_service.Repository;

import com.example.product_service.Entity.CategoryAttributeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryAttributeDefinitionRepository extends JpaRepository<CategoryAttributeDefinition, UUID> {
    List<CategoryAttributeDefinition> findByCategoryId(UUID categoryId);
}
