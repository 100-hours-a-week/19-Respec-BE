package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserVisibilityRequest (
        @NotNull(message = "스펙 공개 여부는 필수입니다.")
        boolean isPublic
) { }
