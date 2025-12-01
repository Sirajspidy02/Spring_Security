package com.Spring_Security.Spring_Security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.redis.RedisCacheManagerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // localhost:6379 OR as per application.properties
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        template.setKeySerializer(org.springframework.data.redis.serializer.RedisSerializer.string());
        template.setValueSerializer(org.springframework.data.redis.serializer.RedisSerializer.json());

        template.setHashKeySerializer(org.springframework.data.redis.serializer.RedisSerializer.string());
        template.setHashValueSerializer(org.springframework.data.redis.serializer.RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager() {
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory())
                .build();
    }
}
