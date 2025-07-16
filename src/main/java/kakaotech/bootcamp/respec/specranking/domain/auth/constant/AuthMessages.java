package kakaotech.bootcamp.respec.specranking.domain.auth.constant;

public class AuthMessages {

    public static final String UNSUPPORTED_OAUTH_PROVIDER = "지원하지 않는 소셜로그인 업체입니다.";
    public static final String EXPIRED_ACCESS_TOKEN = "만료된 액세스 토큰입니다.";
    public static final String INVALID_ACCESS_TOKEN = "유효하지 않은 액세스 토큰입니다.";

    public static final String TOKEN_REFRESH_SUCCESS = "토큰 재발급 성공";

    private AuthMessages() { }
}
