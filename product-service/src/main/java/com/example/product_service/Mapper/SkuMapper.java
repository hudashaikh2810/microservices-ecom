package com.example.product_service.Mapper;

import com.example.product_service.DTO.SKUDto;
import com.example.product_service.Entity.Sku;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring",uses={ProductSummaryMapper.class,ImageMapper.class})
public interface SkuMapper {
    SKUDto convertToDTo(Sku sku);
    Sku convertToEntity(SKUDto skuDto);

}
