package com.example.product_service.Mapper;

import com.example.product_service.DTO.ImageDto;
import com.example.product_service.Entity.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface ImageMapper {
    ImageDto convertToEntity(Image image);
    Image convertToDTO (ImageDto imageDto);
}
