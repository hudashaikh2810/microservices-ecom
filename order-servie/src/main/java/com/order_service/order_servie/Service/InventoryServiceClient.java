package com.order_service.order_servie.Service;

import com.order_service.order_servie.DTO.BulkReservationResponse;
import com.order_service.order_servie.DTO.OrderRequest;
import com.order_service.order_servie.DTO.StockRequest;
import com.order_service.order_servie.DTO.StockReservationRequest;
import com.order_service.order_servie.Exceptions.*;
import com.order_service.order_servie.FeignClients.InventoryServiceFeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

@Service
public class InventoryServiceClient {

    @Autowired
    InventoryServiceFeignClient inventoryServiceFeignClient;

    @CircuitBreaker(name = "inventory-service", fallbackMethod = "callToInventoryServiceFailed")
    @Retry(name = "inventory-service", fallbackMethod = "callToInventoryServiceFailed")
    public BulkReservationResponse callInventoryFeignClient(StockRequest stockReservationRequests) {
        return inventoryServiceFeignClient.reserveInventory(stockReservationRequests);
    }

    public BulkReservationResponse callToInventoryServiceFailed(StockRequest orderRequest, Throwable t) {
        throw new InventoryNotReservedException("Inventory for "+orderRequest.getOrderId()+ " cannot be reserved");
    }

    @CircuitBreaker(name = "inventory-service", fallbackMethod = "confirmBulkOrderFailed")
    @Retry(name = "inventory-service", fallbackMethod = "confirmBulkOrderFailed")

    public String callConfirmBulkReservationRequest(StockRequest stockReservationRequests) {
        return inventoryServiceFeignClient.confirmInventory(stockReservationRequests);
    }

    public String confirmBulkOrderFailed(StockRequest stockReservationRequest, Throwable t) {
        if (t instanceof IllegalArgumentException) {
            throw new IllegalArgumentException(t.getMessage());
        }
        if (t instanceof ReservationExpiredException) {
            throw new ReservationExpiredException(t.getMessage());
        }
        if (t instanceof ReservationNotFoundException) {
            throw new ReservationNotFoundException(t.getMessage());

        }
        if (t instanceof InvalidOrderConfirmationException) {
            throw new InvalidOrderConfirmationException(t.getMessage());
        }
        if (t instanceof InventoryInconsistencyException) {
            throw new InventoryInconsistencyException(t.getMessage());
        }
        if (t instanceof ConcurrentModificationException) {
            throw new ConcurrentModificationException(t.getMessage());

        }
        if (t instanceof InventoryWithSkuNotPresent) {
            throw new InventoryWithSkuNotPresent(t.getMessage());
        }
        if (t instanceof RuntimeException) {
            throw new RuntimeException(t.getMessage());
        }
        return "";
    }


    @CircuitBreaker(name = "inventory-service", fallbackMethod = "confirmReleaseOrderFailed")
    @Retry(name = "inventory-service", fallbackMethod = "confirmReleaseOrderFailed")

    public String callReleaseBulkReservationRequest(StockRequest stockReservationRequests) {

        return inventoryServiceFeignClient.releaseInventory(stockReservationRequests);
    }

    public String releaseBulkOrderFailed(List<StockReservationRequest> stockReservationRequest, Throwable t) {
        if (t instanceof IllegalArgumentException) {
            throw new IllegalArgumentException(t.getMessage());
        }
        if (t instanceof ReservationNotFoundException) {
            throw new ReservationNotFoundException(t.getMessage());
        }
        return "";

    }

    @CircuitBreaker(name="inventory-service",fallbackMethod="cancelFailed")
    public boolean callRestockInventoryOnCancel(String orderId, Map<String,Integer> skuQuantity)
    {
 inventoryServiceFeignClient.cancelOrder(orderId,skuQuantity);
 return true;
    }

    public boolean cancelFailed(String orderId, Map<String,Integer> skuQuantity,Throwable t)
    {
        if(t instanceof OrderAlreadyCancelled)
        {
            throw new OrderAlreadyCancelled(t.getMessage());
        }
        return false;
    }


}
