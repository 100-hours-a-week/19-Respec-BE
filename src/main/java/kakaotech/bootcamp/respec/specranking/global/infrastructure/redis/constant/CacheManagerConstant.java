package kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant;

public class CacheManagerConstant {
    public static final String SPEC_META_DATA_PREFIX = "specMetadata::";
    public static final String SPEC_RANKINGS_PREFIX = "rankings::";
    public static final String SPEC_DETAILS_NAME = "specDetails";
    public static final Long SPEC_META_DATA_CACHING_HOURS = 1L;
    public static final Long SPEC_DETAILS_CACHING_MINUTES = 3L;
    public static final Long TOP_10_RANKINGS_CACHING_MINUTES = 10L;
    public static final Long DEFAULT_CACHING_SECONDS = 30L;
    public static final String CHAT_ENTER_USER_PREFIX = "chat:user:";
    public static final Long CHAT_ENTER_TTL_HOURS = 24L;
}
