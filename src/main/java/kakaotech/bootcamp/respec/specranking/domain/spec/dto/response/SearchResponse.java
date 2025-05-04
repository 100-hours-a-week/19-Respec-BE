package kakaotech.bootcamp.respec.specranking.domain.spec.dto.response;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import lombok.Data;

@Data
public class SearchResponse {
    private Boolean isSuccess;
    private String message;
    private SearchData data;

    @Data
    public static class SearchData {
        private String keyword;
        private List<SearchResult> results;
        private Boolean hasNext;
        private String nextCursor;
    }

    @Data
    public static class SearchResult {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private Long specId;
        private Double score;
        private Long totalRank;
        private Long totalUsersCount;
        private JobField jobField;
        private Long rankByJobField;
        private Long totalUsersCountByJobField;
        private Boolean isBookmarked;
        private Long commentsCount;
        private Long bookmarksCount;
    }

    public SearchResponse(Boolean isSuccess, String message, SearchData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public static SearchResponse success(String keyword, List<SearchResult> results, Boolean hasNext,
                                         String nextCursor) {
        SearchData data = new SearchData();
        data.setKeyword(keyword);
        data.setResults(results);
        data.setHasNext(hasNext);
        data.setNextCursor(nextCursor);

        return new SearchResponse(true, "검색 목록 조회 성공", data);
    }
}
