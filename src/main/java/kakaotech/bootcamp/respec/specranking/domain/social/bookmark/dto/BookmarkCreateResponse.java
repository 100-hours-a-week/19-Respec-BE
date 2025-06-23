package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.dto;

import lombok.Data;

@Data
public class BookmarkCreateResponse {
    private Boolean isSuccess;
    private String message;
    private Long bookmarkId;

    public BookmarkCreateResponse(Boolean isSuccess, String message, Long bookmarkId) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.bookmarkId = bookmarkId;
    }
}
