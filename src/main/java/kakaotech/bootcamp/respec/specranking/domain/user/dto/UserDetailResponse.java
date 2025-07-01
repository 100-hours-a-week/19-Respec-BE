package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import java.time.LocalDateTime;

import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;

public record UserDetailResponse (
        boolean isSuccess,
        String message,
        UserInfoData data
) {
    public record UserInfoData (
            UserDetail user
    ) { }

    public record UserDetail (
            Long id,
            String nickname,
            String profileImageUrl,
            LocalDateTime createdAt,
            String jobField,
            boolean isOpenSpec,
            SpecInfo spec
    ) {
        public static UserDetail from(User user, Spec spec) {
            return new UserDetail(
                    user.getId(),
                    user.getNickname(),
                    user.getUserProfileUrl(),
                    user.getCreatedAt(),
                    extractJobField(spec),
                    user.getIsOpenSpec(),
                    SpecInfo.from(spec)
            );
        }

        private static String extractJobField(Spec spec) {
            return spec != null ? spec.getJobField().getValue() : null;
        }
    }

    public record SpecInfo (
            boolean hasActiveSpec,
            Long activeSpec
    ) {
        public static SpecInfo from(Spec spec) {
            boolean hasActiveSpec = spec != null;
            return new SpecInfo(
                    hasActiveSpec,
                    hasActiveSpec ? spec.getId() : null
            );
        }
    }

    public static UserDetailResponse success(UserDetail user, String message) {
        UserInfoData data = new UserInfoData(user);
        return new UserDetailResponse(true, message, data);
    }
}
