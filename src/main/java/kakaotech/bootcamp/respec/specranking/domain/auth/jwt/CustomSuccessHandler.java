package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.CustomOAuth2User;
import kakaotech.bootcamp.respec.specranking.domain.auth.service.AuthService;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
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

    private static final String TEMP_LOGIN_ID = "TempLoginId";
    private static final Long TEMP_LOGIN_ID_EXP = 1000L * 60 * 5; // 5분
    private static final String ACCESS = "access";
    private static final Long ACCESS_EXP = 1000L * 60 * 10; // 10분

    private final AuthService authService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User userInfo = (CustomOAuth2User) authentication.getPrincipal();
        log.info("OAuth2 로그인 성공 - LoginId: {}", userInfo.getLoginId());

        Optional<User> optUser = userRepository.findByLoginId(userInfo.getLoginId());

        if (optUser.isPresent()) {
            log.info("기존 사용자 로그인 - UserId: {}", optUser.get().getId());

            // 기존 사용자 - JWT 발급
            User user = optUser.get();
            AuthTokenRequestDto requestDto = new AuthTokenRequestDto(user.getId(), user.getLoginId());
            AuthTokenResponseDto responseDto = authService.issueToken(requestDto, false);
            authService.convertTokenToResponse(responseDto, response);

            CookieUtils.addCookie(response, ACCESS, responseDto.accessToken(), (int) (ACCESS_EXP / 1000));
        } else {
            // 신규 사용자 - tempLoginId 쿠키 설정
            String tmpLoginId = userInfo.getProvider() + "_" + userInfo.getProviderId();
            log.info("신규 사용자 - TempLoginId 발급: {}", tmpLoginId);

            CookieUtils.addCookie(response, TEMP_LOGIN_ID, tmpLoginId, (int) (TEMP_LOGIN_ID_EXP / 1000));
        }

        getRedirectStrategy().sendRedirect(request, response, frontendRedirectUrl);
    }
}
