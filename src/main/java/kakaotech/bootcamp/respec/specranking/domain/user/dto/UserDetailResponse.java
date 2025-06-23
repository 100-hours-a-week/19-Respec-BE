package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import java.time.LocalDateTime;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import lombok.Data;

@Data
public class UserDetailResponse {
    private Boolean isSuccess;
    private String message;
    private UserInfoData data;

    @Data
    public static class UserInfoData {
        private UserDetail user;

        public UserInfoData(UserDetail user) {
            this.user = user;
        }
    }

    @Data
    public static class UserDetail {
        private Long id;
        private String nickname;
        private String profileImageUrl;
        private LocalDateTime createdAt;
        private String jobField;
        private Boolean isOpenSpec;
        private SpecInfo spec;

        public UserDetail(Long id, String nickname, String profileImageUrl, LocalDateTime createdAt,
                          String jobField, Boolean isOpenSpec, SpecInfo spec) {
            this.id = id;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
            this.createdAt = createdAt;
            this.jobField = jobField;
            this.isOpenSpec = isOpenSpec;
            this.spec = spec;
        }
    }

    @Data
    public static class SpecInfo {
        private Boolean hasActiveSpec;
        private Long activeSpec;

        public SpecInfo(Boolean hasActiveSpec, Long activeSpec) {
            this.hasActiveSpec = hasActiveSpec;
            this.activeSpec = activeSpec;
        }
    }

    public UserDetailResponse(Boolean isSuccess, String message, UserInfoData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public static UserDetailResponse success(UserDetail user) {
        UserInfoData data = new UserInfoData(user);
        return new UserDetailResponse(true, "사용자 정보 조회 성공", data);
    }

    public static UserDetail createUserDetail(User user, Spec activeSpec) {
        String jobField = activeSpec != null ? activeSpec.getJobField().getValue() : null;
        SpecInfo specInfo = new SpecInfo(
                activeSpec != null,
                activeSpec != null ? activeSpec.getId() : null
        );

        return new UserDetail(
                user.getId(),
                user.getNickname(),
                user.getUserProfileUrl(),
                user.getCreatedAt(),
                jobField,
                user.getIsOpenSpec(),
                specInfo
        );
    }
}
