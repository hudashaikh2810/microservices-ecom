package com.order_service.order_servie.Mapper;

import com.order_service.order_servie.DTO.OrderAddressDto;
import com.order_service.order_servie.Entity.OrderAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderAddressMapper {
    public OrderAddressDto convertToAddressDto(OrderAddress orderAddress);

    public OrderAddress convertToEntity(OrderAddressDto orderAddressDto);
}
