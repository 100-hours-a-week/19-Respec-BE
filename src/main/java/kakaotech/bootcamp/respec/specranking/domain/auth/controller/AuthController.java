package kakaotech.bootcamp.respec.specranking.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.constant.AuthMessages;
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
        authService.reissueTokens(request, response);
        return SimpleResponseDto.success(AuthMessages.TOKEN_REFRESH_SUCCESS);
    }
}
