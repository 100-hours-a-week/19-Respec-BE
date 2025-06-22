package kakaotech.bootcamp.respec.specranking.domain.spec.dto.response;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;

public record RankingResponse(
        Boolean isSuccess,
        String message,
        RankingData data
) {
    public static RankingResponse success(List<RankingItem> rankings, Boolean hasNext, String nextCursor) {
        RankingData data = new RankingData(
                List.copyOf(rankings),
                hasNext,
                nextCursor
        );
        return new RankingResponse(true, "랭킹 목록 조회 성공", data);
    }

    public record RankingData(
            List<RankingItem> rankings,
            Boolean hasNext,
            String nextCursor
    ) {
        public RankingData {
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
            Boolean isBookmarked,
            Long commentsCount,
            Long bookmarksCount
    ) {
    }
}
