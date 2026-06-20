package com.example.product_service.Mapper;

import com.example.product_service.DTO.ProductDto;
import com.example.product_service.Entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses={SkuSummaryMapper.class})
public interface ProductMapper {
    Product convertToEntity(ProductDto productDto);
    ProductDto convertToDto(Product product);

}
