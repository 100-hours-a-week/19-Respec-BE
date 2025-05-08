package kakaotech.bootcamp.respec.specranking.domain.auth.service;

import kakaotech.bootcamp.respec.specranking.domain.auth.dto.CustomOAuth2User;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.KakaoResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.OAuth2Response;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.entity.OAuth;
import kakaotech.bootcamp.respec.specranking.domain.auth.repository.OAuthRepository;
import kakaotech.bootcamp.respec.specranking.domain.common.type.OAuthProvider;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuthRepository oAuthRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;

        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth 공급자입니다.");
        }

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();

        // OAuth 존재 여부 확인 (User와 연결되어 있어야 함)
        Optional<OAuth> optOAuth = oAuthRepository.findByProviderNameAndProviderId(
                OAuthProvider.valueOf(provider), providerId
        );

        User user = optOAuth.map(OAuth::getUser).orElse(null);

        // 인증용 DTO 구성
        AuthenticatedUserDto userDto = AuthenticatedUserDto.builder()
                .id(user != null ? user.getId() : null)
                .loginId(provider + " " + providerId)
                .nickname(user != null ? user.getNickname() : null)
                .userProfileUrl(user != null ? user.getUserProfileUrl() : null)
                .build();

        return new CustomOAuth2User(userDto, oAuth2Response);
    }
}
