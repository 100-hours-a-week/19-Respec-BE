package kakaotech.bootcamp.respec.specranking.global.dto;

public record SimpleResponseDto(
        Boolean isSuccess,
        String message
) {
    public static SimpleResponseDto success(String message) {
        return new SimpleResponseDto(true, message);
    }
}
