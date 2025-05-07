package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String nickname;
    private String userProfileUrl;
    private LocalDateTime createdAt;
}
