package kakaotech.bootcamp.respec.specranking.domain.spec.dto.response;

public record SpecMetaResponse(
        Boolean isSuccess,
        String message,
        Meta data
) {
    public record Meta(
            Long totalUserCount,
            double averageScore
    ) {
    }
}
