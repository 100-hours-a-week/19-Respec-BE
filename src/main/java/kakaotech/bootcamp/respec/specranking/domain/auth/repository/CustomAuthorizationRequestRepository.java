package kakaotech.bootcamp.respec.specranking.domain.auth.repository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final RedisTemplate<String , OAuth2AuthorizationRequest> redisTemplate;

    private static final String STATE_PARAMETER = "state";
    private static final String OAUTH2_AUTHORIZATION_REQUEST_PREFIX = "oauth2:authorization:request:";
    private static final Duration OAUTH2_AUTHORIZATION_REQUEST_EXPIRATION = Duration.ofSeconds(30);

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest httpServletRequest) {

        String state = httpServletRequest.getParameter(STATE_PARAMETER);

        if (state == null) {
            log.warn("OAuth2AuthorizationRequest 로드 실패 - state 파라미터가 없습니다.");
            return null;
        }

        String redisKey = OAUTH2_AUTHORIZATION_REQUEST_PREFIX + state;
        OAuth2AuthorizationRequest authorizationRequest = redisTemplate.opsForValue().get(redisKey);;
        log.info("OAuth2AuthorizationRequest 로드 완료 - state: {}, found: {}", state, authorizationRequest != null);

        return authorizationRequest;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request, HttpServletResponse response) {

        if (authorizationRequest == null) {
            log.warn("OAuth2AuthorizationRequest 저장 실패 - authorizationRequest가 null입니다.");
            return;
        }

        String state = authorizationRequest.getState();
        String redisKey = OAUTH2_AUTHORIZATION_REQUEST_PREFIX + state;

        redisTemplate.opsForValue().set(redisKey, authorizationRequest, OAUTH2_AUTHORIZATION_REQUEST_EXPIRATION);
        log.info("OAuth2AuthorizationRequest 저장 완료 - state: {}", state);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {

        String state = request.getParameter(STATE_PARAMETER);

        if (state == null) {
            log.warn("OAuth2AuthorizationRequest 삭제 실패 - state 파라미터가 없습니다.");
            return null;
        }

        String redisKey = OAUTH2_AUTHORIZATION_REQUEST_PREFIX + state;
        OAuth2AuthorizationRequest authorizationRequest = redisTemplate.opsForValue().get(redisKey);
        log.info("OAuth2AuthorizationRequest 삭제 완료 - state: {}", state);

        return authorizationRequest;
    }
}
