package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserUpdateResponse {
    private Boolean isSuccess;
    private String message;
    private UserUpdateData data;

    @Data
    public static class UserUpdateData {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private LocalDateTime updatedAt;

        public UserUpdateData(Long userId, String nickname, String profileImageUrl, LocalDateTime updatedAt) {
            this.userId = userId;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
            this.updatedAt = updatedAt;
        }

        public static UserUpdateData from(User user) {
            return new UserUpdateData(
                    user.getId(),
                    user.getNickname(),
                    user.getUserProfileUrl(),
                    user.getUpdatedAt()
            );
        }
    }

    public UserUpdateResponse(Boolean isSuccess, String message, UserUpdateData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public static UserUpdateResponse success(User user) {
        return new UserUpdateResponse(true, "회원정보 수정 성공", UserUpdateData.from(user));
    }
}
