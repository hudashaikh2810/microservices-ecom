package com.order_service.order_servie.Mapper;

import com.order_service.order_servie.DTO.OrderItemDto;
import com.order_service.order_servie.Entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemDto toDTO(OrderItem orderItem);

    OrderItem toEntity(OrderItemDto dto);
}

