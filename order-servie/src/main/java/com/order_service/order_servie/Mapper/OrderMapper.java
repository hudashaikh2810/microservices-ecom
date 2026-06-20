package com.order_service.order_servie.Mapper;

import com.order_service.order_servie.DTO.OrderDto;
import com.order_service.order_servie.Entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    OrderDto toDTO(Order order);

    Order toEntity(OrderDto dto);
}

