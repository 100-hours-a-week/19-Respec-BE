package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;

import java.time.LocalDateTime;

public record UserUpdateResponse (
        boolean isSuccess,
        String message,
        UserUpdateData data
) {
    public record UserUpdateData (
            Long userId,
            String nickname,
            String profileImageUrl,
            LocalDateTime updatedAt
    ) {
        public static UserUpdateData from(User user) {
            return new UserUpdateData(
                    user.getId(),
                    user.getNickname(),
                    user.getUserProfileUrl(),
                    user.getUpdatedAt()
            );
        }
    }

    public static UserUpdateResponse success(User user, String message) {
        return new UserUpdateResponse(true, message, UserUpdateData.from(user));
    }
}
