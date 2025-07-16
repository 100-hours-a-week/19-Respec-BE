package kakaotech.bootcamp.respec.specranking.domain.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.cookie.CookieUtils;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenRequest;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.entity.RefreshToken;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.JWTUtil;
import kakaotech.bootcamp.respec.specranking.domain.auth.repository.RefreshTokenRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.common.cookie.CookieConstants;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;
    private final CookieUtils cookieUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public static final String BEARER_PREFIX = "Bearer ";

    public AuthTokenResponse issueToken(AuthTokenRequest request, boolean isReissue) {
        log.info("토큰 발급 시작 - userId: {}, loginId: {}, isReissue: {}", request.userId(), request.loginId(), isReissue);

        String accessToken = jwtUtil.createJwts(CookieConstants.ACCESS_TOKEN, request.userId(), request.loginId(), CookieConstants.ACCESS_TOKEN_EXP);
        String refreshToken = jwtUtil.createJwts(CookieConstants.REFRESH_TOKEN, request.userId(), request.loginId(), CookieConstants.REFRESH_TOKEN_EXP);

        User user = userRepository.findById(request.userId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        saveRefreshTokenToDatabase(user, refreshToken, isReissue);

        log.info("토큰 발급 완료 - userId: {}", request.userId());
        return AuthTokenResponse.of(accessToken, refreshToken);
    }

    public void reissueTokens(HttpServletRequest request, HttpServletResponse response) {
        log.info("토큰 재발급 시작");

        String refreshToken = extractRefreshTokenFromCookie(request);

        validateTokenNotExpired(refreshToken);
        validateTokenCategory(refreshToken);
        validateRefreshTokenExists(refreshToken);

        Long userId = jwtUtil.getUserId(refreshToken);
        String loginId = jwtUtil.getLoginId(refreshToken);

        AuthTokenRequest tokenRequest = new AuthTokenRequest(userId, loginId);
        AuthTokenResponse tokenResponse = issueToken(tokenRequest, true);

        setTokensInResponse(tokenResponse, response);
        log.info("토큰 재발급 완료 - userId: {}", userId);
    }

    public void setTokensInResponse(AuthTokenResponse tokenResponse, HttpServletResponse response) {
        response.setHeader(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + tokenResponse.accessToken());
        cookieUtils.addCookie(response, CookieConstants.REFRESH_TOKEN,
                tokenResponse.refreshToken(), (int) (CookieConstants.REFRESH_TOKEN_EXP / 1000));
    }

    public void deleteExpiredRefreshToken(LocalDateTime currentTime) {
        log.info("만료된 리프레시 토큰 삭제 시작 - currentTime: {}", currentTime);
        int deletedCount = refreshTokenRepository.deleteAllByExpirationBefore(currentTime);
        log.info("만료된 리프레시 토큰 삭제 완료 - 삭제된 개수: {}", deletedCount);
    }

    private void saveRefreshTokenToDatabase(User user, String refreshToken, boolean isReissue) {
        if (isReissue) {
            refreshTokenRepository.deleteAllByUser(user);
            log.info("기존 리프레시 토큰 삭제 완료 - userId: {}", user.getId());
        }

        LocalDateTime expiration = LocalDateTime.now().plus(Duration.ofMillis(CookieConstants.REFRESH_TOKEN_EXP));
        RefreshToken refreshTokenEntity = new RefreshToken(user, refreshToken, expiration);
        refreshTokenRepository.save(refreshTokenEntity);

        log.info("리프레시 토큰 저장 완료 - userId: {}", user.getId());
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        return cookieUtils.getCookie(request, CookieConstants.REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    private void validateTokenNotExpired(String refreshToken) {
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_IS_EXPIRED);
        }
    }

    private void validateTokenCategory(String refreshToken) {
        String tokenCategory = jwtUtil.getCategory(refreshToken);
        if (!CookieConstants.REFRESH_TOKEN.equals(tokenCategory)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void validateRefreshTokenExists(String refreshToken) {
        Boolean exists = refreshTokenRepository.existsByValue(refreshToken);
        if (!exists) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }
}
