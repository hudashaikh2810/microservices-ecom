package com.example.product_service.Entity;

import com.example.product_service.Enums.AttributeDataType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "category_attribute_definition", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"category_id", "attribute_name"})
})
@Getter
@Setter
public class CategoryAttributeDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String attributeName;      // "color", "fit", "material"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttributeDataType dataType;   // ENUM, STRING, NUMBER

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> allowedValues;   // null if dataType != ENUM

    @Column(nullable = false)
    private boolean isRequired;
}
