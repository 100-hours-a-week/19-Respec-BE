package kakaotech.bootcamp.respec.specranking.domain.spec.dto.response;

import lombok.Data;

@Data
public class SpecMetaResponse {
    private boolean isSuccess;
    private String message;
    private Meta data;

    @Data
    public static class Meta {
        private Long totalUserCount;
        private double averageScore;

        public Meta(Long totalUserCount, double averageScore) {
            this.totalUserCount = totalUserCount;
            this.averageScore = averageScore;
        }
    }

    public SpecMetaResponse(boolean isSuccess, String message, Meta data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }
}