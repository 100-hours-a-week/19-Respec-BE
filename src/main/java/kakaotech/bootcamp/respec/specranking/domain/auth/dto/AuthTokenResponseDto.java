package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

import lombok.Data;

@Data
public class AuthTokenResponseDto {

    private final boolean isSuccess;
    private final String message;
    private final Token data;

    @Data
    public static class Token {
        private String accessToken;
        private String refreshToken;
    }

    public AuthTokenResponseDto(boolean isSuccess, String message, Token data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public static AuthTokenResponseDto success(String accessToken, String refreshToken) {
        Token data = new Token();
        data.setAccessToken(accessToken);
        data.setRefreshToken(refreshToken);

        return new AuthTokenResponseDto(true, "로그인 성공", data);
    }

    public String accessToken() { return data.getAccessToken(); }
    public String refreshToken() { return data.getRefreshToken(); }
}
