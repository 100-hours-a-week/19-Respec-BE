package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AuthTokenRequest(
        @NotNull(message = "사용자 id는 필수입니다.")
        @Positive(message = "id는 양수여야 합니다.")
        Long userId,

        @NotBlank(message = "loginId는 필수입니다.")
        String loginId
) { }
