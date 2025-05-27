package kakaotech.bootcamp.respec.specranking.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.service.AuthService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token/refresh")
    public SimpleResponseDto reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            authService.reissueTokens(request, response);
            return new SimpleResponseDto(true, "토큰 재발급 성공");
        } catch (IllegalArgumentException e) {
            return new SimpleResponseDto(false, e.getMessage());
        }
    }
}
