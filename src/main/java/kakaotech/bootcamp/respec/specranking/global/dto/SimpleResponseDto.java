package kakaotech.bootcamp.respec.specranking.global.dto;

import lombok.Data;

@Data
public class SimpleResponseDto {
    private boolean isSuccess;
    private String message;

    public SimpleResponseDto(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}

