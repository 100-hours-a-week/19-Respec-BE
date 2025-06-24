package kakaotech.bootcamp.respec.specranking.domain.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.cookie.CookieUtils;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.RefreshTokenCreateDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.entity.RefreshToken;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.JWTUtil;
import kakaotech.bootcamp.respec.specranking.domain.auth.repository.RefreshTokenRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;
    private final CookieUtils cookieUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    private static final Long ACCESS_EXP = 1000L * 60 * 10; // 10분
    private static final Long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7; // 7일

    public AuthTokenResponseDto issueToken(AuthTokenRequestDto dto, boolean isReissue) {
        String access = jwtUtil.createJwts(ACCESS, dto.getId(), dto.getLoginId(), ACCESS_EXP);
        String refresh = jwtUtil.createJwts(REFRESH, dto.getId(), dto.getLoginId(), REFRESH_EXP);

        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        saveRefreshToken(user, refresh, isReissue);

        return AuthTokenResponseDto.success(access, refresh);
    }

    public void reissueTokens(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieUtils.getCookie(request, REFRESH)
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException("refresh 토큰이 없습니다."));

        validateExpired(refresh);
        validateCategory(refresh);
        validateRefreshExists(refresh);

        Long userId = jwtUtil.getUserId(refresh);
        String loginId = jwtUtil.getLoginId(refresh);

        AuthTokenResponseDto dto = issueToken(new AuthTokenRequestDto(userId, loginId), true);
        convertTokenToResponse(dto, response);
    }

    public void convertTokenToResponse(AuthTokenResponseDto dto, HttpServletResponse response) {
        response.setHeader(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + dto.accessToken());
        cookieUtils.addCookie(response, REFRESH, dto.refreshToken(), (int) (REFRESH_EXP / 1000));
    }

    public void deleteByExpirationBefore(LocalDateTime currentTime) {
        refreshTokenRepository.deleteAllByExpirationBefore(currentTime);
    }

    private void saveRefreshToken(User user, String refresh, boolean isReissue) {
        if (isReissue) {
            refreshTokenRepository.deleteAllByUser(user);
        }

        LocalDateTime expiration = LocalDateTime.now().plus(Duration.ofMillis(REFRESH_EXP));
        RefreshTokenCreateDto dto = new RefreshTokenCreateDto(user, refresh, expiration);
        RefreshToken refreshToken = new RefreshToken(dto.getUser(), dto.getValue(), dto.getExpiration());
        refreshTokenRepository.save(refreshToken);
    }

    private void validateExpired(String refresh) {
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("refresh 토큰이 만료되었습니다.");
        }
    }

    private void validateCategory(String refresh) {
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals(REFRESH)) {
            throw new IllegalArgumentException("유효하지 않은 refresh 토큰입니다.");
        }
    }

    private void validateRefreshExists(String refresh) {
        Boolean isExist = refreshTokenRepository.existsByValue(refresh);
        if (!isExist) {
            throw new IllegalArgumentException("refresh 토큰이 DB에 존재하지 않습니다.");
        }
    }
}
