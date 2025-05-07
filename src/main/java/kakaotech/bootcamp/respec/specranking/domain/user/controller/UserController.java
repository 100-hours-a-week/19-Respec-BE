package kakaotech.bootcamp.respec.specranking.domain.user.controller;

import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @RequestBody UserSignupRequestDto request,
            @AuthenticationPrincipal AuthenticatedUserDto userDto) {
        request.setLoginId(userDto.getLoginId());
        UserResponseDto user = userService.signup(request);
        return ResponseEntity.ok(user);
    }

    // 내 기본정보 조회
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal AuthenticatedUserDto userDto) {
        // 회원가입이 완료되지 않은 사용자
        if (userDto.getId() == null) {
            String loginId = userDto.getLoginId();
            if (loginId == null || !loginId.contains(" ")) {
                return ResponseEntity.badRequest().body(Map.of("message", "잘못된 loginId 형식입니다."));
            }

            String[] parts = loginId.split(" ", 2);
            String provider = parts[0];
            String providerId = parts[1];

            return ResponseEntity.ok(Map.of(
                    "loginId", loginId,
                    "provider", provider,
                    "providerId", providerId
            ));
        }

        // 회원가입 완료된 사용자
        return ResponseEntity.ok(userService.getUserInfo(userDto.getId()));
    }

    // 사용자 기본정보 조회
    @GetMapping("/{userId}")
    public UserResponseDto getUserInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

    // 회원탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal AuthenticatedUserDto userDto) {
        userService.deleteUser(userDto.getId());
        return ResponseEntity.noContent().build();
    }
}
