package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || header.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = header.substring(BEARER_PREFIX.length()).trim();

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            response.addHeader("Token-Error", "Expired");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 access 토큰입니다.");
            return;
        }

        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {
            response.addHeader("Token-Error", "Invalid");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 access 토큰입니다.");
            return;
        }

        Long userId = jwtUtil.getUserId(accessToken);
        String loginId = jwtUtil.getLoginId(accessToken);

        AuthenticatedUserDto authUser = new AuthenticatedUserDto();
        authUser.setId(userId);
        authUser.setLoginId(loginId);

        Authentication auth = new UsernamePasswordAuthenticationToken(authUser, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
