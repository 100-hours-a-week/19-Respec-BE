package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.constant.AuthMessages;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUser;
import kakaotech.bootcamp.respec.specranking.global.common.cookie.CookieConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_ERROR_HEADER = "Token-Error";
    private static final String EXPIRED_TOKEN_ERROR = "Expired";
    private static final String INVALID_TOKEN_ERROR = "Invalid";

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!hasValidAuthorizationHeader(authorizationHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            if (!validateAccessToken(accessToken, response)) return;

            authenticateUser(accessToken);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            handleExpiredToken(response);
        } catch (Exception e) {
            handleInvalidToken(response);
        }
    }

    private boolean hasValidAuthorizationHeader(String header) {
        return header != null && !header.isBlank() && header.startsWith(BEARER_PREFIX);
    }

    private boolean validateAccessToken(String accessToken, HttpServletResponse response) throws IOException {
        if (jwtUtil.isExpired(accessToken)) {
            handleExpiredToken(response);
            return false;
        }

        String tokenCategory = jwtUtil.getTokenCategory(accessToken);
        if (!CookieConstants.ACCESS_TOKEN.equals(tokenCategory)) {
            handleInvalidToken(response);
            return false;
        }

        return true;
    }

    private void authenticateUser(String accessToken) {
        Long userId = jwtUtil.getUserId(accessToken);
        String loginId = jwtUtil.getLoginId(accessToken);

        AuthenticatedUser authenticatedUser = AuthenticatedUser.of(userId, loginId, null, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticatedUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleExpiredToken(HttpServletResponse response) throws IOException {
        response.addHeader(TOKEN_ERROR_HEADER, EXPIRED_TOKEN_ERROR);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, AuthMessages.EXPIRED_ACCESS_TOKEN);
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        response.addHeader(TOKEN_ERROR_HEADER, INVALID_TOKEN_ERROR);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, AuthMessages.INVALID_ACCESS_TOKEN);
    }
}
