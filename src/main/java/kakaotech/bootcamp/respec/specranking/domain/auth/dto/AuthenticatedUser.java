package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

public record AuthenticatedUser (
        Long id,
        String loginId,
        String nickname,
        String userProfileUrl
) {
    public static AuthenticatedUser of(Long id, String loginId, String nickname, String userProfileUrl) {
        return new AuthenticatedUser(id, loginId, nickname, userProfileUrl);
    }
}
