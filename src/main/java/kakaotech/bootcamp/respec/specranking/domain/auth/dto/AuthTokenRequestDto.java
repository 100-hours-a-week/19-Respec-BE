package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AuthTokenRequestDto {
    @NotNull(message = "사용자 id는 필수입니다.")
    @Positive(message = "id는 양수여야 합니다.")
    private Long id;

    @NotBlank(message = "loginId는 필수입니다.")
    private String loginId;

    public AuthTokenRequestDto(Long id, String loginId) {
        this.id = id;
        this.loginId = loginId;
    }
}
