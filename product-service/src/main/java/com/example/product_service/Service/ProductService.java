package com.example.product_service.Service;

import com.example.product_service.DTO.*;
import com.example.product_service.Entity.Image;
import com.example.product_service.Entity.Product;
import com.example.product_service.Enums.Category;
import com.example.product_service.Exception.ProductWithIdNotFound;
import com.example.product_service.Mapper.ProductMapper;
import com.example.product_service.Repository.ProductRepository;
import com.example.product_service.wrapper.RestPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    final private static Logger log = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisService redisService;

    public ProductDto addProduct(ProductDto productDto) {
        Product p = productMapper.convertToEntity(productDto);

        Product savedProduct = productRepository.save(p);
        log.info("Product saved successfully");
        return productMapper.convertToDto(savedProduct);
    }

    public void deleteProduct(long productId) throws RuntimeException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductWithIdNotFound("Product not found"));
        productRepository.delete(product);
    }

    public ProductDto getProduct(long productId) {
        return productRepository.findById(productId).map(p -> productMapper.convertToDto(p))
                .orElseThrow(() -> new ProductWithIdNotFound("Product with id not found"));
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto updateProduct(long productId, ProductDto productDto) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product with id not found"));
        log.info("Product with id exists");
        p.setProductDescription(productDto.getProductDescription());
        p.setCategory(productDto.getCategory());
        p = productRepository.save(p);
        log.info("Product updated successfully");
        return productMapper.convertToDto(p);
    }


    @Transactional(readOnly = true)
    public RestPage<ProductMetaDto> findAll(int page, int size, String sortBy, String sortDir) {
        String key = redisService.getHashKeyForPage(sortBy, page, size);
        List<Long> productIdList = redisService.getProductListFromRedis(key);
        List<ProductMetaDto> productMetaDto = new ArrayList<>();
        if (productIdList != null && !productIdList.isEmpty()) {

            productMetaDto = productIdList.stream().map(id -> {

                String key1 = redisService.getKeyForProduct(id);
                ProductMetaDto productMetaData = redisService.getProductMetaDto(key1);
                if (productMetaData == null) {
                    productMetaData = productRepository.findByCategoryNameWithDefaultSku(Category.valueOf(sortBy),id).orElse(null);
                }
                if(productMetaData!=null)
                {
                    redisService.putToRedis(key1,productMetaData);
                }
                return productMetaData;
            }).filter(Objects::nonNull).toList();
            return new RestPage<>(productMetaDto, PageRequest.of(page, size), productMetaDto.size());
        } else {
            Page<ProductMetaDto> productPage = productRepository.findByCategoryNameWithDefaultSkuPage(Category.valueOf(sortBy), PageRequest.of(page, size));
            productIdList = productPage.getContent().stream().map(ProductMetaDto::getId).toList();
            List<ProductMetaDto> metaDto = productPage.getContent().stream().toList();
            redisService.putToRedis(key, productIdList);
            metaDto.forEach(p -> redisService.putToRedis(redisService.getKeyForProduct(p.getId()), p));
            return new RestPage<>(productPage.getContent(), PageRequest.of(page, size), productPage.getTotalElements());

        }
    }



}
