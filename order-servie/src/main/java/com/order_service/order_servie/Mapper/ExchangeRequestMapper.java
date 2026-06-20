package com.order_service.order_servie.Mapper;

import com.order_service.order_servie.DTO.ExchangeRequestDto;
import com.order_service.order_servie.Entity.ExchangeRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, OrderMapper.class})
public interface ExchangeRequestMapper {

    ExchangeRequestDto toDTO(ExchangeRequest entity);

    ExchangeRequest toEntity(ExchangeRequestDto dto);
}

