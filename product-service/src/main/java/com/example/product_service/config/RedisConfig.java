package com.example.product_service.config;

import com.example.product_service.DTO.ProductMetaDto;
import com.example.product_service.Entity.Product;
import com.example.product_service.wrapper.RestPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ProductMetaDto> redisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        RedisTemplate<String,ProductMetaDto> template=new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());   // ✅ important
        return template;

    }

    @Bean
    RedisTemplate<String, List<Long>> redisTemplateListId(RedisConnectionFactory redisConnectionFactory)
    {
        RedisTemplate<String,List<Long>> template=new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
