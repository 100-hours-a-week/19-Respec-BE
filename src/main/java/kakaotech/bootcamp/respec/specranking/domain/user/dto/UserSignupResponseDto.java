package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSignupResponseDto {

    private final boolean isSuccess;
    private final String  message;
    private final UserResponseDto data;

    /* 정적 팩터리 – 성공 */
    public static UserSignupResponseDto success(UserResponseDto user) {
        return UserSignupResponseDto.builder()
                .isSuccess(true)
                .message("회원가입 성공")
                .data(user)
                .build();
    }

    /* 정적 팩터리 – 실패 */
    public static UserSignupResponseDto fail(String msg) {
        return UserSignupResponseDto.builder()
                .isSuccess(false)
                .message(msg)
                .data(null)
                .build();
    }
}
