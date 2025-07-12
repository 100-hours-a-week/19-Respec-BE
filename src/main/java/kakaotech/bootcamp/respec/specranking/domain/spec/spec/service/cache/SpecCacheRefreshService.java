package kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.cache;

import java.time.Duration;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedRankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SpecMetaResponse.Meta;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.refresh.SpecRefreshQueryService;
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
        Meta meta = specRefreshQueryService.getMetaDataFromDb(jobField);
        redisTemplate.opsForValue().set("specMetadata::" + jobField.name(), meta, Duration.ofHours(1));
    }

    @Async
    public void refreshRankingCache(JobField jobField, int limit) {
        CachedRankingResponse rankingData = specRefreshQueryService.getRankingDataFromDb(jobField, limit);
        String cacheKey = "rankings::" + jobField.name() + "::" + limit;
        redisTemplate.opsForValue().set(cacheKey, rankingData, Duration.ofMinutes(10));
    }
}
