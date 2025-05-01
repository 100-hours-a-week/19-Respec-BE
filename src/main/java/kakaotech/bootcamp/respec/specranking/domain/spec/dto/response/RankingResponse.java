package kakaotech.bootcamp.respec.specranking.domain.spec.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class RankingResponse {
    private boolean isSuccess;
    private String message;
    private RankingData data;

    @Data
    public static class RankingData {
        private List<RankingItem> rankings;
        private boolean hasNext;
        private String nextCursor;
    }

    @Data
    public static class RankingItem {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private Long specId;
        private String jobField;
        private double averageScore;
        private int rankByJobField;
        private int totalUsersCountByJobField;
        private int rank;
        private boolean isBookmarked;
        private int commentsCount;
        private int bookmarksCount;
    }
    
    public RankingResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
    
    public RankingResponse(boolean isSuccess, String message, RankingData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }
    
    public static RankingResponse success(List<RankingItem> rankings, boolean hasNext, String nextCursor) {
        RankingData data = new RankingData();
        data.setRankings(rankings);
        data.setHasNext(hasNext);
        data.setNextCursor(nextCursor);
        
        return new RankingResponse(true, "랭킹 목록 조회 성공", data);
    }
    
    public static RankingResponse fail(String message) {
        return new RankingResponse(false, message);
    }
}
