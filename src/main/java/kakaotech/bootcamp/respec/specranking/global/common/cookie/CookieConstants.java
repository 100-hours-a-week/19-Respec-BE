package kakaotech.bootcamp.respec.specranking.global.common.cookie;

public class CookieConstants {

    public static final String ACCESS_TOKEN = "access";
    public static final String REFRESH_TOKEN = "refresh";
    public static final String TEMP_LOGIN_ID = "TempLoginId";

    public static final long ACCESS_TOKEN_EXP = 1000L * 60 * 10; // 10 minutes
    public static final long REFRESH_TOKEN_EXP = 1000L * 60 * 60 * 24 * 7; // 7 days
    public static final long TEMP_LOGIN_ID_EXP = 1000L * 60 * 5;

    private CookieConstants() { }
}
