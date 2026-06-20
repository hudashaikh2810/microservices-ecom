package com.example.product_service.Mapper;

import com.example.product_service.DTO.ProductDto;
import com.example.product_service.DTO.ProductSummaryDto;
import com.example.product_service.Entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel="Spring")
public interface ProductSummaryMapper {
    ProductSummaryDto convertToDto(Product product);
    Product convertToEntity(ProductSummaryDto productSummaryDto);
}
