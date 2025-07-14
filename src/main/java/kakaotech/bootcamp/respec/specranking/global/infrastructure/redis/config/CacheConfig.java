package kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.config;

import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.DEFAULT_CACHING_SECONDS;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.SPEC_DETAILS_CACHING_MINUTES;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.SPEC_DETAILS_KEY;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.SPEC_META_DATA_CACHING_SECONDS;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.SPEC_META_DATA_KEY;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.TOP_10_RANKINGS_CACHING_SECONDS;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.TOP_10_RANKINGS_KEY;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put(SPEC_META_DATA_KEY,
                createCacheConfig(Duration.ofSeconds(SPEC_META_DATA_CACHING_SECONDS)));
        cacheConfigurations.put(SPEC_DETAILS_KEY,
                createCacheConfigWithNullValues(Duration.ofMinutes(SPEC_DETAILS_CACHING_MINUTES)));
        cacheConfigurations.put(TOP_10_RANKINGS_KEY,
                createCacheConfig(Duration.ofSeconds(TOP_10_RANKINGS_CACHING_SECONDS)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(createCacheConfig(Duration.ofMinutes(DEFAULT_CACHING_SECONDS)))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    private RedisCacheConfiguration createCacheConfig(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    private RedisCacheConfiguration createCacheConfigWithNullValues(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
