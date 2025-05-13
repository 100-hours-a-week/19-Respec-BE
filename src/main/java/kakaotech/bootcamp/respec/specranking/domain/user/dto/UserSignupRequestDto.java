package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserSignupRequestDto {
    @Setter
    private String loginId;
    private String nickname;
    private String userProfileUrl;
}
