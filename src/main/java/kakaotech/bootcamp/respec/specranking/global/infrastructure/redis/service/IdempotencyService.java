package kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.service;

import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.IdempotencyConstant.DUMMY_VALUE;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;

    public Boolean setIfAbsent(String key, Duration ttl) {
        return redisTemplate.opsForValue().setIfAbsent(key, DUMMY_VALUE, ttl);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

}
