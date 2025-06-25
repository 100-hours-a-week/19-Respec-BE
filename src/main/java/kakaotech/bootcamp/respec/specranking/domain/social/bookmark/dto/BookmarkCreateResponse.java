package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.dto;

public record BookmarkCreateResponse (
        Boolean isSuccess,
        String message,
        Long bookmarkId
) {
    public static BookmarkCreateResponse success(String message, Long bookmarkId) {
        return new BookmarkCreateResponse(true, message, bookmarkId);
    }
}
