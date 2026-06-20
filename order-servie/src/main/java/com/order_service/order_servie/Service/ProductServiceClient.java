package com.order_service.order_servie.Service;

import com.order_service.order_servie.DTO.ProductPrice;
import com.order_service.order_servie.Exceptions.SkuWithIdNotFound;
import com.order_service.order_servie.FeignClients.ProductServiceFeignClient;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class ProductServiceClient {

    @Autowired
    private ProductServiceFeignClient productServiceFeignClient;

    @CircuitBreaker(name = "product-service", fallbackMethod = "callProductPriceFailed")
    @Retry(name = "product-service", fallbackMethod = "callProductPriceFailed")
    public List<ProductPrice> callProductServiceFeignClient(List<String> skuId) {
        return productServiceFeignClient.getPrice(skuId);
    }

    public List<ProductPrice> callProductPriceFailed(List<String> skuId, Exception e) throws Exception {
        log.error("Product service call failed for skuIds: {}", skuId, e);

        // Business exception - propagate it
        if (e instanceof SkuWithIdNotFound ||
                (e.getCause() instanceof SkuWithIdNotFound)) {
            throw new SkuWithIdNotFound("Sku with id not found: " + skuId);
        }

        // Infrastructure failures - return graceful degradation
        if (e instanceof CallNotPermittedException) {
            log.warn("Circuit brekaer open for product-service");
            throw new Exception("Cannot connect to servcice.There is something wrong");
        } else if (e instanceof TimeoutException) {
            log.warn("Timeout product-service didnt respond under definite time");
            throw new Exception("Cannot connect to servcice.There is something wrong.Service Did not resposne.Timeout");
        }

         throw new Exception("Could not procced with order creating.Try again");
    }
}
