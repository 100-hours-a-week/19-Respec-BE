package kakaotech.bootcamp.respec.specranking.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.CookieUtils;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.JWTUtil;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    // 회원가입
    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody UserSignupRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {

        if (request.getLoginId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("loginId가 필요합니다.");
        }

        // user 생성
        UserResponseDto user = userService.signup(request);

        // JWT 생성 (userId, loginId 포함)
        String token = jwtUtil.createJwts(user.getId(), request.getLoginId(), 1000L * 60 * 60);

        // TempLoginId 쿠키 삭제
        CookieUtils.deleteCookie(httpRequest, response, "TempLoginId");

        // 새 authorization 쿠키 설정
        CookieUtils.addCookie(response, "Authorization", token, 60 * 60 * 24 * 7);

        return ResponseEntity.ok(user);
    }

    // 사용자 기본정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable String userId) {
        Long id;

        // me이면 현재 로그인한 사용자 id 추출, 숫자인 경우 타인 id 조회
        if ("me".equals(userId)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserDto userDto)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "isSuccess", false,
                        "message", "로그인 정보가 없습니다."
                ));
            }

            id = userDto.getId();
        } else {
            try {
                id = Long.parseLong(userId);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "isSuccess", false,
                        "message", "유효하지 않은 userId입니다."
                ));
            }
        }

        // 사용자 정보 조회
        Map<String, Object> userData = userService.getUserInfo(id);

        return ResponseEntity.ok(Map.of(
                "isSuccess", true,
                "message", "정보 조회 성공",
                "data", Map.of("user", userData)
        ));
    }

    // 회원탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal AuthenticatedUserDto userDto) {
        userService.deleteUser(userDto.getId());
        return ResponseEntity.noContent().build();
    }
}

class NicknameCheckResponse {
    private boolean available;

    public NicknameCheckResponse(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }
}

class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}