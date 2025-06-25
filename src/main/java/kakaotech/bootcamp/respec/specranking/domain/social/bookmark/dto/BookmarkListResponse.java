package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.dto;

import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;

import java.util.List;

public record BookmarkListResponse (
        Boolean isSuccess,
        String message,
        BookmarkListData data
) {
    public record BookmarkListData (
            List<BookmarkItem> bookmarks,
            Boolean hasNext,
            String nextCursor
    ) {}

    public record BookmarkItem (
        Long id,
        SpecInfo spec
    ) {}

    public record SpecInfo (
            Long id, Long userId, String nickname, String profileImageUrl,
            Double score, Long totalRank, Long totalUserCount,
            Long jobFieldRank, Long jobFieldUserCount, JobField jobField,
            Boolean isBookmarked, Long commentsCount, Long bookmarksCount
    ) {}

    public static BookmarkListResponse success(List<BookmarkItem> bookmarks, Boolean hasNext, String nextCursor, String message) {
        BookmarkListData bookmarkListData = new BookmarkListData(bookmarks, hasNext, nextCursor);
        return new BookmarkListResponse(true, message, bookmarkListData);
    }
}
