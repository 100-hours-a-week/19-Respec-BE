package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 쿠키를 모두 불러와 authorization key에 담긴 쿠키 찾기
        String authorization = null;
        Cookie[] cookies = request.getCookies();
        System.out.println("======= 쿠키 목록 ======");
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + " = " + cookie.getValue());
                if (cookie.getName().equals("Authorization")) {
                    authorization = cookie.getValue();
                    break;
                }
            }
        } else {
            System.out.println("쿠키 없음");
        }

        // authorization 헤더 검증
        if (authorization == null) {
            System.out.println("token null");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization;

        // 토큰 소멸시간 검증
        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");

            filterChain.doFilter(request, response);
            return;
        }

        // userId, loginId 파싱
        String userIdStr = jwtUtil.getUserId(token);
        String loginId = jwtUtil.getLoginId(token);

        if (loginId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 인증 객체 생성
        AuthenticatedUserDto userDto = new AuthenticatedUserDto();
        userDto.setLoginId(loginId);

        if (userIdStr != null) {
            try {
                userDto.setId(Long.valueOf(userIdStr));
            } catch (NumberFormatException ignored) {
                // 유효하지 않은 userId는 무시하고 로그인 ID 기반으로만 인증
            }
        }

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDto,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
