package kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;


public record CachedRankingResponse(
        List<CachedRankingItem> items,
        boolean hasNext,
        String nextCursor) {

    public record CachedRankingItem(
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
            Long bookmarksCount) {
    }
}
