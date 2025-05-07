package kakaotech.bootcamp.respec.specranking.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // OAuth 로그인 후 JWT 토큰 발급
    @PostMapping
    public ResponseEntity<AuthTokenResponseDto> issueToken(@RequestBody AuthTokenRequestDto dto) {
        AuthTokenResponseDto token = authService.issueToken(dto);
        return ResponseEntity.ok(token);
    }

    // 로그아웃
    @DeleteMapping("/token")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.noContent().build();
    }

//    // 엑세스 토큰 재발급
//    @PostMapping("/token/refresh")
//    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
//        String newAccessToken = authService.refreshAccessToken(refreshToken.replace("Bearer ", ""));
//        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
//    }
}
