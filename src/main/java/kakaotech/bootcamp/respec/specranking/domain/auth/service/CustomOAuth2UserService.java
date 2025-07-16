package kakaotech.bootcamp.respec.specranking.domain.auth.service;

import kakaotech.bootcamp.respec.specranking.domain.auth.constant.AuthMessages;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.CustomOAuth2User;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.KakaoResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.OAuth2Response;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUser;
import kakaotech.bootcamp.respec.specranking.domain.auth.entity.OAuth;
import kakaotech.bootcamp.respec.specranking.domain.auth.repository.OAuthRepository;
import kakaotech.bootcamp.respec.specranking.global.common.type.OAuthProvider;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kakaotech.bootcamp.respec.specranking.global.common.type.OAuthProvider.kakao;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuthRepository oAuthRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2 사용자 정보 로딩 시작");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        log.info("OAuth2 제공자 - registrationId: {}", registrationId);

        OAuth2Response oAuth2Response = createOAuth2Response(registrationId, oAuth2User);
        User user = findUserByOAuthInfo(oAuth2Response);
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(user, oAuth2Response);

        log.info("OAuth2 사용자 정보 로딩 완료 - provider: {}, providerId: {}, userId: {}",
                oAuth2Response.getProvider(), oAuth2Response.getProviderId(), user != null ? user.getId() : null);

        return new CustomOAuth2User(authenticatedUser, oAuth2Response);
    }

    private OAuth2Response createOAuth2Response(String registrationId, OAuth2User oAuth2User) {
        if (kakao.name().equals(registrationId)) {
            return new KakaoResponse(oAuth2User.getAttributes());
        }

        throw new OAuth2AuthenticationException(AuthMessages.UNSUPPORTED_OAUTH_PROVIDER);
    }

    private User findUserByOAuthInfo(OAuth2Response oAuth2Response) {
        Optional<OAuth> optOAuth = oAuthRepository.findByProviderNameAndProviderId(
                OAuthProvider.valueOf(oAuth2Response.getProvider()),
                oAuth2Response.getProviderId()
        );

        return optOAuth.map(OAuth::getUser).orElse(null);
    }

    private AuthenticatedUser createAuthenticatedUser(User user, OAuth2Response oAuth2Response) {
        String loginId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        return AuthenticatedUser.of(
                user != null ? user.getId() : null,
                loginId,
                user != null ? user.getNickname() : null,
                user != null ? user.getUserProfileUrl() : null
        );
    }
}
