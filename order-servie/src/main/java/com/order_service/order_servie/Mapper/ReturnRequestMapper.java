package com.order_service.order_servie.Mapper;

import com.order_service.order_servie.DTO.ReturnRequestDto;
import com.order_service.order_servie.Entity.ReturnRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, OrderMapper.class})
public interface ReturnRequestMapper {

    ReturnRequestDto toDTO(ReturnRequest entity);

    ReturnRequest toEntity(ReturnRequestDto dto);
}

