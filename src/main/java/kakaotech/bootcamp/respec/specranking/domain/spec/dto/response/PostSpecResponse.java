package kakaotech.bootcamp.respec.specranking.domain.spec.dto.response;

import lombok.Data;

@Data
public class PostSpecResponse {
    private boolean isSuccess;
    private String message;

    public PostSpecResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}