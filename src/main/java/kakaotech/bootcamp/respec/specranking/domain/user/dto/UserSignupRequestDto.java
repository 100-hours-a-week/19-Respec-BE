package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UserSignupRequestDto {
    @Setter
    private String loginId;
    private String nickname;
    private String userProfileUrl;
}
