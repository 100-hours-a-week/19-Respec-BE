package kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;

public record RankingResponse(
        Boolean isSuccess,
        String message,
        SpecRankings data
) {
    public record SpecRankings(
            List<RankingItem> rankings,
            Boolean hasNext,
            String nextCursor
    ) {
        public SpecRankings {
            rankings = List.copyOf(rankings);
        }
    }

    public record RankingItem(
            Long userId,
            String nickname,
            String profileImageUrl,
            Long specId,
            Double score,
            Long totalRank,
            Long totalUsersCount,
            JobField jobField,
            Long rankByJobField,
            Long usersCountByJobField,
            Long commentsCount,
            Long bookmarksCount
    ) {
    }
}
