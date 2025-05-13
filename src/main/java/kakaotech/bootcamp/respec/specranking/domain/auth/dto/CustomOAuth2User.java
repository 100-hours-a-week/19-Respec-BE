package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class CustomOAuth2User implements OAuth2User {

    private final AuthenticatedUserDto userDto;
    private final Collection<? extends GrantedAuthority> authorities;
    private final OAuth2Response oAuth2Response;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(AuthenticatedUserDto userDto, OAuth2Response oAuth2Response) {
        this.userDto = userDto;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        // attributes 설정
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", userDto.getId());
        attributes.put("loginId", userDto.getLoginId());
        attributes.put("nickname", userDto.getNickname());
        attributes.put("userProfileUrl", userDto.getUserProfileUrl());
        this.attributes = attributes;

        this.oAuth2Response = oAuth2Response;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public String getLoginId() {
        return userDto.getLoginId();
    }

    @Override
    public String getName() { return userDto.getLoginId(); }

    public Long getId() {
        return userDto.getId();
    }

    public String getProvider() {
        return oAuth2Response != null ? oAuth2Response.getProvider() : null;
    }

    public String getProviderId() {
        return oAuth2Response != null ? oAuth2Response.getProviderId() : null;
    }
}
