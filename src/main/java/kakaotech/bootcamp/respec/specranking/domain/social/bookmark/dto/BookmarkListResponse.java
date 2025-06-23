package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.dto;

import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import lombok.Data;

import java.util.List;

@Data
public class BookmarkListResponse {
    private Boolean isSuccess;
    private String message;
    private BookmarkListData data;

    @Data
    public static class BookmarkListData {
        private List<BookmarkItem> bookmarks;
        private Boolean hasNext;
        private String nextCursor;

        public BookmarkListData(List<BookmarkItem> bookmarks, Boolean hasNext, String nextCursor) {
            this.bookmarks = bookmarks;
            this.hasNext = hasNext;
            this.nextCursor = nextCursor;
        }
    }

    @Data
    public static class BookmarkItem {
        private Long id;
        private SpecInfo spec;

        public BookmarkItem(Long id, SpecInfo spec) {
            this.id = id;
            this.spec = spec;
        }
    }

    @Data
    public static class SpecInfo {
        private Long id;
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private Double score;
        private Long totalRank;
        private Long totalUserCount;
        private Long jobFieldRank;
        private Long jobFieldUserCount;
        private JobField jobField;
        private Boolean isBookmarked;
        private Long commentsCount;
        private Long bookmarksCount;

        public SpecInfo(Long id, Long userId, String nickname, String profileImageUrl, Double score,
                        Long totalRank, Long totalUserCount, Long jobFieldRank, Long jobFieldUserCount,
                        JobField jobField, Boolean isBookmarked, Long commentsCount, Long bookmarksCount) {
            this.id = id;
            this.userId = userId;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
            this.score = score;
            this.totalRank = totalRank;
            this.totalUserCount = totalUserCount;
            this.jobFieldRank = jobFieldRank;
            this.jobFieldUserCount = jobFieldUserCount;
            this.jobField = jobField;
            this.isBookmarked = isBookmarked;
            this.commentsCount = commentsCount;
            this.bookmarksCount = bookmarksCount;
        }
    }

    public BookmarkListResponse(Boolean isSuccess, String message, BookmarkListData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public static BookmarkListResponse success(List<BookmarkItem> bookmarks, Boolean hasNext, String nextCursor) {
        BookmarkListData data = new BookmarkListData(bookmarks, hasNext, nextCursor);
        return new BookmarkListResponse(true, "즐겨찾기 목록 조회 성공", data);
    }
}
