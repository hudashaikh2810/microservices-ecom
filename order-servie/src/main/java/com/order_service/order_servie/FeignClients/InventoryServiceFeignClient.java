package com.order_service.order_servie.FeignClients;

import com.order_service.order_servie.Config.FeignConfig;
import com.order_service.order_servie.DTO.BulkReservationResponse;
import com.order_service.order_servie.DTO.OrderRequest;
import com.order_service.order_servie.DTO.StockRequest;
import com.order_service.order_servie.DTO.StockReservationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name="inventory-service",configuration= FeignConfig.class)
public interface InventoryServiceFeignClient {
    @GetMapping("/api/inventories/reserve/bulk")
    public BulkReservationResponse reserveInventory(StockRequest stockReservationRequest);

    @PostMapping("/api/inventories/confirm/bulk")
    public String confirmInventory(StockRequest stockReservationRequests);

    @PostMapping("/api/inventories/release/bulk")
    public String releaseInventory(StockRequest stockReservationRequests);

    @PostMapping("/api/inventories/cancel/{id}")
    public String cancelOrder(@PathVariable("id") String id,@RequestBody Map<String,Integer> skuQuantity);


}

