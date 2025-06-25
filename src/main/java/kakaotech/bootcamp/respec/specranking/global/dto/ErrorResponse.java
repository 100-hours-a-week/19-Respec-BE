package kakaotech.bootcamp.respec.specranking.global.dto;

import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;

public record ErrorResponse (
        Boolean isSuccess,
        String message
) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(false, errorCode.getMessage());
    }
}
