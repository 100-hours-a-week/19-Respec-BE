package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

import lombok.Getter;

@Getter
public class AuthTokenRequestDto {
    private String provider;
    private String providerId;
}
