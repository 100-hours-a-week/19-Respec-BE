package kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant;

public class CacheManagerConstant {
    public static final String SPEC_META_DATA_KEY = "specMetadata";
    public static final String SPEC_DETAILS_KEY = "specDetails";
    public static final String TOP_10_RANKINGS_KEY = "top10Rankings";
    public static final Long SPEC_META_DATA_CACHING_SECONDS = 1800L;
    public static final Long SPEC_DETAILS_CACHING_MINUTES = 3L;
    public static final Long TOP_10_RANKINGS_CACHING_SECONDS = 30L;
    public static final Long DEFAULT_CACHING_SECONDS = 30L;
}
