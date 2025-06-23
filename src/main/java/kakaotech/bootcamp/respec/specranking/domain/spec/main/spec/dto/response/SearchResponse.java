package kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.dto.response;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;

public record SearchResponse(
        Boolean isSuccess,
        String message,
        SearchData data
) {
    public static SearchResponse success(String keyword, List<SearchResult> results, Boolean hasNext,
                                         String nextCursor) {
        return new SearchResponse(
                true,
                "검색 목록 조회 성공",
                new SearchData(keyword, List.copyOf(results), hasNext, nextCursor)
        );
    }

    public record SearchData(
            String keyword,
            List<SearchResult> results,
            Boolean hasNext,
            String nextCursor
    ) {
        public SearchData {
            results = List.copyOf(results); // 방어적 복사
        }
    }

    public record SearchResult(
            Long userId,
            String nickname,
            String profileImageUrl,
            Long specId,
            Double score,
            Long totalRank,
            Long totalUsersCount,
            JobField jobField,
            Long rankByJobField,
            Long totalUsersCountByJobField,
            Boolean isBookmarked,
            Long commentsCount,
            Long bookmarksCount
    ) {
    }
}
