package kakaotech.bootcamp.respec.specranking.domain.auth.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface CookieUtils {

    Optional<Cookie> getCookie(HttpServletRequest request, String cookieName);

    void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge);

    void deleteCookie(HttpServletResponse response, String cookieName);
}
