package kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache;

public record CachedMetaDto(
        long computeTime,
        CachedMeta data
) {
    public record CachedMeta(
            Long totalUserCount,
            double averageScore
    ) {
    }
}
