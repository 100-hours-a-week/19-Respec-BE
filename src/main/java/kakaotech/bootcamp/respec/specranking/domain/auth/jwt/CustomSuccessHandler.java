package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.CustomOAuth2User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String loginId = customUserDetails.getLoginId();

        // 신규 사용자 판별 → 리디렉션 분기
        boolean isNewUser = !userRepository.existsByLoginId(loginId);

        if (isNewUser) {
            String tmpLoginId = customUserDetails.getProvider() + "_" + customUserDetails.getProviderId();

            // 임시 쿠키로 loginId 전달
            Cookie loginIdCookie = new Cookie("TempLoginId", tmpLoginId);
            loginIdCookie.setPath("/");
            loginIdCookie.setHttpOnly(false);
            loginIdCookie.setMaxAge(300);
            response.addCookie(loginIdCookie);

            response.sendRedirect("http://localhost:3000/profile-setup");
        } else {
            response.sendRedirect("http://localhost:3000/oauth2/callback");
        }
    }
}
