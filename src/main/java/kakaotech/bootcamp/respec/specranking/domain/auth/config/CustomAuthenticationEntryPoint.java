package kakaotech.bootcamp.respec.specranking.domain.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");

        // Ajax 또는 fetch 요청인 경우 → JSON 401
        if (uri.startsWith("/api/") || (accept != null && accept.contains("application/json"))) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"Unauthorized\"}");
        }
        // 그 외에는 (브라우저 직접 접근) → 리디렉션 허용
        else {
            response.sendRedirect("/login");
        }
    }
}
