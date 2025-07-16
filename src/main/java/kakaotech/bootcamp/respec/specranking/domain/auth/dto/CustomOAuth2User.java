package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class CustomOAuth2User implements OAuth2User {

    private final AuthenticatedUser authenticatedUser;
    private final OAuth2Response oAuth2Response;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(AuthenticatedUser authenticatedUser, OAuth2Response oAuth2Response) {
        this.authenticatedUser = authenticatedUser;
        this.oAuth2Response = oAuth2Response;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        this.attributes = createAttributes(authenticatedUser);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return authenticatedUser.loginId();
    }

    public Long getId() {
        return authenticatedUser.id();
    }

    public String getLoginId() {
        return authenticatedUser.loginId();
    }

    public String getProvider() {
        return oAuth2Response.getProvider();
    }

    public String getProviderId() {
        return oAuth2Response.getProviderId();
    }

    private Map<String, Object> createAttributes(AuthenticatedUser authenticatedUser) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", authenticatedUser.id());
        attributes.put("loginId", authenticatedUser.loginId());
        attributes.put("nickname", authenticatedUser.nickname());
        attributes.put("userProfileUrl", authenticatedUser.userProfileUrl());
        return Collections.unmodifiableMap(attributes);
    }
}
