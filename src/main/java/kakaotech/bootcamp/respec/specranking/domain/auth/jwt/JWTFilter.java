package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<Cookie> authCookie = CookieUtils.getCookie(request, "Authorization");

        if (authCookie.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authCookie.get().getValue();

        // 토큰 소멸시간 검증
        try {
            if (jwtUtil.isExpired(token)) {
                String loginId = jwtUtil.getLoginId(token);

                if (loginId != null) {
                    Optional<User> optUser = userRepository.findByLoginId(loginId);

                    if (optUser.isPresent()) {
                        User user = optUser.get();
                        String newToken = jwtUtil.createJwts(user.getId(), user.getLoginId(), 1000L * 60 * 60 * 24);
                        CookieUtils.addCookie(response, "Authorization", newToken, 60 * 60 * 24);
                        setAuthenticationContext(user.getId(), user.getLoginId());
                    }
                }
            } else {
                Long userId = jwtUtil.getUserId(token);
                String loginId = jwtUtil.getLoginId(token);

                if (loginId != null) {
                    setAuthenticationContext(userId, loginId);
                }
            }
        } catch (Exception e) {
            logger.error("JWT Token processing error", e);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(Long userId, String loginId) {
        AuthenticatedUserDto userDto = new AuthenticatedUserDto();
        userDto.setLoginId(loginId);

        if (userId != null) {
            userDto.setId(userId);
        }

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDto,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
