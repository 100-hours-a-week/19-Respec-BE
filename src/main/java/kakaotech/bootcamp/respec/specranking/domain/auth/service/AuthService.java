package kakaotech.bootcamp.respec.specranking.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.entity.OAuth;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.JWTUtil;
import kakaotech.bootcamp.respec.specranking.domain.auth.repository.OAuthRepository;
import kakaotech.bootcamp.respec.specranking.domain.common.type.OAuthProvider;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final OAuthRepository oAuthRepository;
    private final JWTUtil jwtUtil;

    // JWT 발급
    public AuthTokenResponseDto issueToken(AuthTokenRequestDto dto) {
        // provider + providerId로 OAuth 사용자 찾기
        Optional<OAuth> optOAuth = oAuthRepository.findByProviderNameAndProviderId(
                OAuthProvider.valueOf(dto.getProvider()),
                dto.getProviderId()
        );

        if (optOAuth.isEmpty()) {
            throw new RuntimeException("OAuth 정보가 존재하지 않습니다.");
        }

        OAuth oAuth = optOAuth.get();
        User user = oAuth.getUser();

        if (user == null) {
            throw new RuntimeException("아직 회원가입이 완료되지 않은 사용자입니다.");
        }

        // 유효기간 1시간인 엑세스 토큰 발급
        String accessToken = jwtUtil.createJwts(String.valueOf(user.getId()), user.getLoginId(), 1000L * 60 * 60);
        // 유효기간 24시간인 리프레시 토큰 발급
        String refreshToken = jwtUtil.createJwts(String.valueOf(user.getId()), user.getLoginId(), 1000L * 60 * 60 * 24);

        return new AuthTokenResponseDto(accessToken, refreshToken);
    }

//    // 엑세스 토큰 재발급
//    public String refreshAccessToken(String refreshToken) {
//        if (jwtUtil.isExpired(refreshToken)) {
//            throw new IllegalArgumentException("리프레시 토큰이 만료되었습니다.");
//        }
//
//        String userId = jwtUtil.getUserId(refreshToken);
//
//        return jwtUtil.createJwts(userId, 1000L * 60 * 60);
//    }

    // 로그아웃
    public void logout(HttpServletResponse response) {
        // 쿠키 삭제를 위해 만료시간을 0으로 설정한 쿠키 생성
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
    }
}
