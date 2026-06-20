package com.example.product_service.Mapper;

import com.example.product_service.DTO.SkuSummaryDto;
import com.example.product_service.Entity.Sku;
import org.mapstruct.Mapper;

@Mapper(componentModel="Spring",uses = {ImageMapper.class})
public interface SkuSummaryMapper {
    SkuSummaryDto convertToDto(Sku sku);
    Sku convertToEntity(SkuSummaryDto skuSummaryDto);
}
