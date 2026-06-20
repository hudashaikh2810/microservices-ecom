package com.Ecom.cart_service.cart_service.Service;

import com.Ecom.cart_service.cart_service.DTO.CartItemDetailDto;
import com.Ecom.cart_service.cart_service.DTO.ProductDetailDto;
import com.Ecom.cart_service.cart_service.Entity.Cart;
import com.Ecom.cart_service.cart_service.Exception.SkuWithIdNotFound;
import com.Ecom.cart_service.cart_service.FeignClient.ProductServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductClientService {
    private static final Logger log = LoggerFactory.getLogger(ProductClientService.class);

    @Autowired
    private ProductServiceClient productServiceClient;

    @CircuitBreaker(name = "productService", fallbackMethod = "failedToFetchProductDetail")
    public CartItemDetailDto fetchProductDetail(String skuId) {
        ProductDetailDto productDetailDto = productServiceClient.getProductDetail(skuId);
        productDetailDto.setSkuId(skuId);
        return CartItemDetailDto.builder().productName(productDetailDto.getProductName())
                .skuId(skuId).pricePerUnit(productDetailDto.getPrice())
                .coverImage(productDetailDto.getCoverImageUrl()).build();

    }

    public CartItemDetailDto failedToFetchProductDetail(String skuId, Throwable t) {
        if (t instanceof SkuWithIdNotFound) {
            throw (SkuWithIdNotFound) t;
        }
        log.info("Fetching product detail from product service for skuId {} failed", skuId);
        return CartItemDetailDto.builder().productName(null)
                .skuId(skuId).coverImage(null).pricePerUnit(null).build();
    }
    @CircuitBreaker(name = "productService", fallbackMethod = "failedGetPrice")
    public Double getPrice(String skuId, Cart cart) {
        return productServiceClient.getPriceOfProduct(skuId);
    }

    public Double failedGetPrice(String skuId, Cart cart, Throwable t) {

        if (t instanceof SkuWithIdNotFound) {
            throw (SkuWithIdNotFound) t;
        }

        // For other exceptions (circuit open, timeout, etc.), return null or default
        log.error("Failed to get price for SKU: {}. Error: {}", skuId, t.getMessage());

        return null;
    }
}
