package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUserDto {
    // 인증된 사용자의 정보를 담는 dto
    private Long id;
    private String loginId;
    private String nickname;
    private String userProfileUrl;
}
