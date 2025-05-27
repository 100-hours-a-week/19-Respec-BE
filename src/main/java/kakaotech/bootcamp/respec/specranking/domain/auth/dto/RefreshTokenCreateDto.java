package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RefreshTokenCreateDto {
    private final User user;
    private final String value;
    private final LocalDateTime expiration;
}
