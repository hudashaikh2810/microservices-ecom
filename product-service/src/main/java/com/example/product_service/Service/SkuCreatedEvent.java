package com.example.product_service.Service;

import com.example.product_service.DTO.SKUDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SkuCreatedEvent {
    private final Logger log= LoggerFactory.getLogger(SkuCreatedEvent.class);
    @Autowired
    KafkaTemplate<String,SKUDto> kafkaTemplate;
    public void SkuCreated(SKUDto skuDto)
    {
        log.info("Going to send notificaation for {}",skuDto.getId());
        kafkaTemplate.send("sku-created",skuDto);
        log.info("Notification sent successfully");
    }
}
