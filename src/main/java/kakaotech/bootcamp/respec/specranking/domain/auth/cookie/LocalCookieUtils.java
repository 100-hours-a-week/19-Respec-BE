package kakaotech.bootcamp.respec.specranking.domain.auth.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@Profile("local")
public class LocalCookieUtils implements CookieUtils {

    private static final class CookieConfig {
        static final boolean IS_SECURE = false;
        static final boolean IS_HTTP_ONLY = false;
        static final String DEFAULT_PATH = "/";
        static final String SAME_SITE_CROSS_DOMAIN = "Lax";
        static final String DOMAIN = "localhost";
    }

    @Override
    public Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst();
    }

    @Override
    public void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {
        try {
            ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue).maxAge(maxAge).build();
        } catch (Exception e) {
            log.error("쿠키 설정 실패 - Name: {}, Value 길이: {}", cookieName, cookieValue.length(), e);
            throw new
        }
    }
}
