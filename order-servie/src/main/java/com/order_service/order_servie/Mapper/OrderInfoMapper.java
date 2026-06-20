package com.order_service.order_servie.Mapper;

import com.order_service.order_servie.DTO.OrderInfoDto;
import com.order_service.order_servie.Entity.OrderInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderInfoMapper {

    OrderInfoDto toDTO(OrderInfo orderInfo);

    OrderInfo toEntity(OrderInfoDto dto);
}

