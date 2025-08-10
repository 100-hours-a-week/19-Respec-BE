package kakaotech.bootcamp.respec.specranking.domain.auth.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@Profile("!local")
public class ProdCookieUtils implements CookieUtils {

    private static final class CookieConfig {
        static final boolean IS_SECURE = true;
        static final boolean IS_HTTP_ONLY = false;
        static final String DEFAULT_PATH = "/";
        static final String SAME_SITE_CROSS_DOMAIN = "None";
        static final String DOMAIN = ".specranking.net";
    }

    @Override
    public Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();

        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst();
    }

    @Override
    public void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {
        ResponseCookie newCookie = createResponseCookie(cookieName, cookieValue, maxAge);
        response.addHeader(HttpHeaders.SET_COOKIE, newCookie.toString());
    }

    @Override
    public void deleteCookie(HttpServletResponse response, String cookieName) {
        ResponseCookie deleteCookie = deleteResponseCookie(cookieName);
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }

    public ResponseCookie createResponseCookie(String cookieName, String cookieValue, int maxAge) {
        return ResponseCookie.from(cookieName, cookieValue)
                .domain(CookieConfig.DOMAIN)
                .path(CookieConfig.DEFAULT_PATH)
                .maxAge(maxAge)
                .secure(CookieConfig.IS_SECURE)
                .httpOnly(CookieConfig.IS_HTTP_ONLY)
                .sameSite(CookieConfig.SAME_SITE_CROSS_DOMAIN)
                .build();
    }

    public ResponseCookie deleteResponseCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .domain(CookieConfig.DOMAIN)
                .path(CookieConfig.DEFAULT_PATH)
                .maxAge(0)
                .secure(CookieConfig.IS_SECURE)
                .httpOnly(CookieConfig.IS_HTTP_ONLY)
                .sameSite(CookieConfig.SAME_SITE_CROSS_DOMAIN)
                .build();
    }
}
