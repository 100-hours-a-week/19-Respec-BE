package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import jakarta.servlet.ServletException;
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

    private static final String TEMP_LOGIN_ID_COOKIE_NAME = "TempLoginId";
    private static final String IS_NEW_USER_COOKIE_NAME = "IsNewUser";

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String loginId = customUserDetails.getLoginId();
        boolean isNewUser = !userRepository.existsByLoginId(loginId);
        String tmpLoginId = customUserDetails.getProvider() + "_" + customUserDetails.getProviderId();

        // 쿠키 생성
        CookieUtils.addCookie(response, TEMP_LOGIN_ID_COOKIE_NAME, tmpLoginId, 5 * 60);
        CookieUtils.addCookie(response, IS_NEW_USER_COOKIE_NAME, String.valueOf(isNewUser), 5 * 60);

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/oauth-redirect");
    }
}
