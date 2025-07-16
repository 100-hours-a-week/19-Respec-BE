package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import kakaotech.bootcamp.respec.specranking.domain.auth.cookie.CookieUtils;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenRequest;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.CustomOAuth2User;
import kakaotech.bootcamp.respec.specranking.domain.auth.service.AuthService;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.common.cookie.CookieConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${frontend.redirect-url}")
    private String frontendRedirectUrl;

    private final AuthService authService;
    private final UserRepository userRepository;
    private final CookieUtils cookieUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        log.info("OAuth2 로그인 성공 - LoginId: {}, URI: {}, QueryString: {}",
                oAuth2User.getLoginId(), request.getRequestURI(), request.getQueryString());

        Optional<User> optUser = userRepository.findByLoginId(oAuth2User.getLoginId());

        if (optUser.isPresent()) {
            handleExistingUserLogin(optUser.get(), response);
        } else {
            handleNewUserLogin(oAuth2User, response);
        }

        getRedirectStrategy().sendRedirect(request, response, frontendRedirectUrl);
    }

    private void handleExistingUserLogin(User user, HttpServletResponse response) {
        log.info("기존 사용자 로그인 - UserId: {}", user.getId());

        AuthTokenRequest tokenRequest = new AuthTokenRequest(user.getId(), user.getLoginId());
        AuthTokenResponse tokenResponse = authService.issueToken(tokenRequest, false);

        authService.setTokensInResponse(tokenResponse, response);
        cookieUtils.addCookie(response, CookieConstants.ACCESS_TOKEN,
                tokenResponse.accessToken(), (int) (CookieConstants.ACCESS_TOKEN_EXP / 1000));
    }

    private void handleNewUserLogin(CustomOAuth2User oAuth2User, HttpServletResponse response) {
        String tempLoginId = oAuth2User.getProvider() + "_" + oAuth2User.getProviderId();
        log.info("신규 사용자 - TempLoginId 발급: {}", tempLoginId);

        cookieUtils.addCookie(response, CookieConstants.TEMP_LOGIN_ID,
                tempLoginId, (int) (CookieConstants.TEMP_LOGIN_ID_EXP / 1000));
    }
}
