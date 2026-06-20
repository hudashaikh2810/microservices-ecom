package com.example.product_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.Optional;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void testRedis()
    {
        redisTemplate.opsForValue().set("email","shaikhhuda2810@gmail.com");
     var email=   redisTemplate.opsForValue().get("email");
        Assert.notNull(email,"Null");
        if(email.toString().equals("shaikhhuda2810@gmail.com"))
        {
            System.out.println("Huda");
        }

    }
}
