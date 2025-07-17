package kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.cache;

import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.SPEC_META_DATA_CACHING_HOURS;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.SPEC_META_DATA_PREFIX;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.SPEC_RANKINGS_PREFIX;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.TOP_10_RANKINGS_CACHING_MINUTES;

import java.time.Duration;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedMetaDto;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedRankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.cache.refresh.SpecRefreshQueryService;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecCacheRefreshService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SpecRefreshQueryService specRefreshQueryService;

    @Async
    public void refreshSpecMetadata(JobField jobField) {
        CachedMetaDto cachedMetaResponse = specRefreshQueryService.getMetaDataFromDb(jobField);
        redisTemplate.opsForValue().set(SPEC_META_DATA_PREFIX + jobField.name(), cachedMetaResponse,
                Duration.ofHours(SPEC_META_DATA_CACHING_HOURS));
    }

    @Async
    public void refreshRankingCache(JobField jobField, int limit) {
        CachedRankingResponse rankingData = specRefreshQueryService.getRankingDataFromDb(jobField, limit);
        String cacheKey = SPEC_RANKINGS_PREFIX + jobField.name() + "::" + limit;
        redisTemplate.opsForValue().set(cacheKey, rankingData, Duration.ofMinutes(TOP_10_RANKINGS_CACHING_MINUTES));
    }
}
