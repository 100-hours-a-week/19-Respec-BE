package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(min = 2, max = 11, message = "닉네임은 2자 이상 11자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "닉네임은 영문, 숫자, 한글만 사용 가능합니다.")
    private String nickname;

    public Boolean hasNickname() {
        return nickname != null && !nickname.trim().isEmpty();
    }
}
