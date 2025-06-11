package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserVisibilityRequest {

    @NotNull(message = "공개 여부는 필수입니다.")
    private Boolean isPublic;

    public UserVisibilityRequest(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
