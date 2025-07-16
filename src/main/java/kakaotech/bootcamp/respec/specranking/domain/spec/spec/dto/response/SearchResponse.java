package kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;

public record SearchResponse(
        Boolean isSuccess,
        String message,
        SearchData data
) {
    public record SearchData(
            String keyword,
            List<SearchResult> results,
            Boolean hasNext,
            String nextCursor
    ) {
        public SearchData {
            results = List.copyOf(results);
        }
    }

    public record SearchResult(
            Long userId, String nickname,
            String profileImageUrl, Long specId,
            Double score, Long totalRank,
            Long totalUsersCount, JobField jobField,
            Long rankByJobField, Long totalUsersCountByJobField,
            Long commentsCount,
            Long bookmarksCount
    ) {
    }
}
