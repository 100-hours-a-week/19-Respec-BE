package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
