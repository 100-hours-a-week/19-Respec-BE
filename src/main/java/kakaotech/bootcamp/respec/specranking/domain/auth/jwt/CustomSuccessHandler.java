package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.CustomOAuth2User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("✅ [SuccessHandler] 진입");

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        String loginId = customUserDetails.getLoginId();

        String token = jwtUtil.createJwts(String.valueOf(userId), loginId, 60*60*1000L);

        response.addCookie(createCookie("Authorization", token));

        // 신규 사용자 판별 → 리디렉션 분기
        boolean isNewUser = !userRepository.existsByLoginId(loginId);

        System.out.println("얏호얏호얏호호잇짜");
        if (isNewUser) {
            System.out.println("얏호얏호얏호호잇짜1111");
            response.sendRedirect("http://localhost:3000/profile-setup");
        } else {
            System.out.println("얏호얏호얏호호잇짜2222");
            response.sendRedirect("http://localhost:3000/oauth2/callback");
        }
        System.out.println("얏호얏호얏호");
    }

    private Cookie createCookie(String key, String value) {
        System.out.println("야호야호야호야호야호");
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60);
//        cookie.setSecure(true);  // https일 때 활성화, http일 때는 필요없음
        cookie.setPath("/");
//        cookie.setHttpOnly(true);

        return cookie;
    }
}
