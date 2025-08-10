package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;

import java.time.LocalDateTime;

public record UserSignupResponse (
        boolean isSuccess,
        String  message,
        UserSignupData data
) {
    public record UserSignupData (
        Long id,
        String nickname,
        String profileImageUrl,
        LocalDateTime createdAt
    ) {
        public static UserSignupData from(User user) {
            return new UserSignupData(
                    user.getId(),
                    user.getNickname(),
                    user.getUserProfileUrl(),
                    user.getCreatedAt()
            );
        }
    }

    public static UserSignupResponse success(User user, String message) {
        return new UserSignupResponse(true, message, UserSignupData.from(user));
    }
}
