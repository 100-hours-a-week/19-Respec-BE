package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import kakaotech.bootcamp.respec.specranking.domain.auth.dto.CustomOAuth2User;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${frontend.redirect-url}")
    private String frontendRedirectUrl;

    private static final String TEMP_LOGIN_ID_COOKIE_NAME = "TempLoginId";

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String loginId = customUserDetails.getLoginId();

        Optional<User> optUser = userRepository.findByLoginId(loginId);

        if (optUser.isPresent()) {
            // 기존 사용자 - JWT 발급
            User user = optUser.get();
            String token = jwtUtil.createJwts(user.getId(), user.getLoginId(), 1000L * 60 * 60 * 24);
            CookieUtils.addCookie(response, "Authorization", token, 60 * 60 * 24);
        } else {
            // 신규 사용자 - tempLoginId 쿠키 설정
            String tmpLoginId = customUserDetails.getProvider() + "_" + customUserDetails.getProviderId();
            CookieUtils.addCookie(response, TEMP_LOGIN_ID_COOKIE_NAME, tmpLoginId, 5 * 60);
        }

        getRedirectStrategy().sendRedirect(request, response, frontendRedirectUrl);
    }
}
