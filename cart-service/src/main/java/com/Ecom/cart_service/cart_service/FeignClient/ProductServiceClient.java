package com.Ecom.cart_service.cart_service.FeignClient;

import com.Ecom.cart_service.cart_service.DTO.ProductDetailDto;
import com.Ecom.cart_service.cart_service.config.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="product-service",configuration = FeignConfig.class)
public interface ProductServiceClient {
    @GetMapping("/product/sku/price/{id}")
    Double getPriceOfProduct(@PathVariable String id);

    @GetMapping("/product/sku/detail/{id}")
    ProductDetailDto getProductDetail(@PathVariable String id);
}
