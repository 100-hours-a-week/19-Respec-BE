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
    ) { }

    public record SpecInfo (
            boolean hasActiveSpec,
            Long activeSpec
    ) { }

    public static UserDetailResponse success(UserDetail user, String message) {
        UserInfoData data = new UserInfoData(user);
        return new UserDetailResponse(true, message, data);
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
