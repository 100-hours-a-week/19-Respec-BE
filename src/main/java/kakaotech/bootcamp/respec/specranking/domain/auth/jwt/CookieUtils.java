package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

public class CookieUtils {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        try {
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);

            // Secure, HttpOnly, SameSite=None, Domain 명시
            String cookieString = String.format(
                    "%s=%s; Max-Age=%d; Path=/; Domain=dev.specranking.net; Secure; HttpOnly; SameSite=None",
                    name, encodedValue, maxAge
            );

            response.addHeader("Set-Cookie", cookieString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode cookie", e);
        }
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        // 삭제 시에도 동일한 도메인, Secure, SameSite 설정 필요
        String cookieString = String.format(
                "%s=; Max-Age=0; Path=/; Domain=dev.specranking.net; Secure; HttpOnly; SameSite=None",
                name
        );
        response.addHeader("Set-Cookie", cookieString);
    }
}
