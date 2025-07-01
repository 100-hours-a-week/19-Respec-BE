package kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.cache;

import java.time.Duration;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SpecMetaResponse.Meta;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.refresh.SpecDbQueryService;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecCacheRefreshService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SpecDbQueryService specDbQueryService;

    @Async
    public void refreshSpecMetadata(JobField jobField) {
        Meta meta = specDbQueryService.getMetaDataFromDb(jobField);
        redisTemplate.opsForValue().set("specMetadata::" + jobField.name(), meta, Duration.ofHours(1));
    }
}
