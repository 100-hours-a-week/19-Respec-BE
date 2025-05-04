package kakaotech.bootcamp.respec.specranking.domain.spec.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class SearchResponse {
    private boolean isSuccess;
    private String message;
    private SearchData data;

    @Data
    public static class SearchData {
        private String keyword;
        private List<SearchResult> results;
        private boolean hasNext;
        private String nextCursor;
    }

    @Data
    public static class SearchResult {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private Long specId;
        private String jobField;
        private double totalAnalyzeScore;
        private Long rankByJobField;
        private int totalUsersCountByJobField;
        private Long rank;
        private boolean isBookmarked;
        private int commentsCount;
        private int bookmarksCount;
    }

    public SearchResponse(boolean isSuccess, String message, SearchData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public static SearchResponse success(String keyword, List<SearchResult> results, boolean hasNext,
                                         String nextCursor) {
        SearchData data = new SearchData();
        data.setKeyword(keyword);
        data.setResults(results);
        data.setHasNext(hasNext);
        data.setNextCursor(nextCursor);

        return new SearchResponse(true, "검색 목록 조회 성공", data);
    }
}
