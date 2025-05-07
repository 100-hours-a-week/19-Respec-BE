package kakaotech.bootcamp.respec.specranking.domain.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        System.out.println("❌ OAuth2 로그인 실패: " + exception.getMessage());
        exception.printStackTrace();  // 전체 스택 출력
        response.sendRedirect("http://localhost:3000/login?error");
    }
}
