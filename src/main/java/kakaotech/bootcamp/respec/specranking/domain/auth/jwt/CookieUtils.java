package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class CookieUtils {

    private static final class CookieConfig {
        static final boolean IS_LOCAL = false;

        static final boolean IS_SECURE = !IS_LOCAL;
        static final boolean IS_HTTP_ONLY = false;
        static final String DEFAULT_PATH = "/";
        static final String SAME_SITE_CROSS_DOMAIN = IS_LOCAL ? "Lax" : "None";
        static final String DOMAIN = IS_LOCAL ? "localhost" : ".dev.specranking.net";
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        try {
            ResponseCookie cookie = ResponseCookie.from(name, value)
                            .domain(CookieConfig.DOMAIN)
                            .path(CookieConfig.DEFAULT_PATH)
                            .maxAge(maxAge)
                            .secure(CookieConfig.IS_SECURE)
                            .httpOnly(CookieConfig.IS_HTTP_ONLY)
                            .sameSite(CookieConfig.SAME_SITE_CROSS_DOMAIN)
                            .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            log.info("쿠키 설정 완료 - Name: {}, Value 길이: {}, MaxAge: {}초", name, value.length(), maxAge);
            log.debug("쿠키 상세: {}", cookie);

        } catch (Exception e) {
            log.error("쿠키 설정 실패 - Name: {}, Value 길이: {}", name, value.length(), e);
            throw new RuntimeException("Failed to set cookie", e);
        }
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        try {
            ResponseCookie deleteCookie = ResponseCookie.from(name, "")
                    .domain(CookieConfig.DOMAIN)
                    .path(CookieConfig.DEFAULT_PATH)
                    .maxAge(0)
                    .secure(CookieConfig.IS_SECURE)
                    .httpOnly(CookieConfig.IS_HTTP_ONLY)
                    .sameSite(CookieConfig.SAME_SITE_CROSS_DOMAIN)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
            log.info("쿠키 삭제 완료 - Name: {}", name);
            log.debug("삭제 쿠키 상세: {}", deleteCookie);

        } catch (Exception e) {
            log.error("쿠키 삭제 실패 - Name: {}", name, e);
            throw new RuntimeException("Failed to delete cookie", e);
        }
    }
}
