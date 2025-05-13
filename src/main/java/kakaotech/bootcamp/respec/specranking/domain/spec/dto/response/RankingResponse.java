package kakaotech.bootcamp.respec.specranking.domain.spec.dto.response;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import lombok.Data;

@Data
public class RankingResponse {
    private Boolean isSuccess;
    private String message;
    private RankingData data;

    @Data
    public static class RankingData {
        private List<RankingItem> rankings;
        private Boolean hasNext;
        private String nextCursor;
    }

    @Data
    public static class RankingItem {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private Long specId;
        private Double score;
        private Long totalRank;
        private Long totalUsersCount;
        private JobField jobField;
        private Long rankByJobField;
        private Long usersCountByJobField;
        private Boolean isBookmarked;
        private Long commentsCount;
        private Long bookmarksCount;
    }

    public RankingResponse(Boolean isSuccess, String message, RankingData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public static RankingResponse success(List<RankingItem> rankings, Boolean hasNext, String nextCursor) {
        RankingData data = new RankingData();
        data.setRankings(rankings);
        data.setHasNext(hasNext);
        data.setNextCursor(nextCursor);

        return new RankingResponse(true, "랭킹 목록 조회 성공", data);
    }
}
