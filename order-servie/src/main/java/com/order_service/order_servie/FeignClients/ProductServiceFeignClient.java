package com.order_service.order_servie.FeignClients;

import com.order_service.order_servie.Config.FeignConfig;
import com.order_service.order_servie.DTO.ProductPrice;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name="product-service",configuration = FeignConfig.class)
public interface ProductServiceFeignClient {
@GetMapping("/product/sku/price")
    public List<ProductPrice> getPrice(List<String> sku);
}
