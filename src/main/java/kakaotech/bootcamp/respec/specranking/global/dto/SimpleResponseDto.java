package kakaotech.bootcamp.respec.specranking.global.dto;

import lombok.Data;

@Data
public class SimpleResponseDto {
    private Boolean isSuccess;
    private String message;

    public SimpleResponseDto(Boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}

