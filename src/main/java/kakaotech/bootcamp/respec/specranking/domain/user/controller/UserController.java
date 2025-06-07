package kakaotech.bootcamp.respec.specranking.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.CookieUtils;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.JWTUtil;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserUpdateRequest;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserUpdateResponse;
import kakaotech.bootcamp.respec.specranking.domain.user.service.UserService;
import kakaotech.bootcamp.respec.specranking.domain.user.util.DuplicateNicknameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    // 허용된 이미지 타입
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png"
    );

    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // 회원가입 (멀티파트 폼 데이터)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUser(
            @RequestPart("nickname") String nickname,
            @RequestPart("loginId") String loginId,
            @RequestPart(value = "userProfileUrl", required = false) MultipartFile profileImage,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {

        if (loginId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "isSuccess", false,
                    "message", "loginId가 필요합니다."
            ));
        }

        // 이미지 파일이 있는 경우 유효성 검사
        if (profileImage != null && !profileImage.isEmpty()) {
            // 파일 타입 검사
            String contentType = profileImage.getContentType();
            if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(Map.of(
                        "isSuccess", false,
                        "message", "PNG 또는 JPG 형식의 이미지만 업로드 가능합니다."
                ));
            }

            // 파일 크기 검사
            if (profileImage.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of(
                        "isSuccess", false,
                        "message", "이미지 크기는 10MB 이하여야 합니다."
                ));
            }
        }

        try {
            UserSignupRequestDto requestDto = new UserSignupRequestDto(loginId, nickname, null);
            UserResponseDto user = userService.signup(requestDto, profileImage);

            // JWT 생성 (userId, loginId 포함)
            String token = jwtUtil.createJwts(user.getId(), loginId, 1000L * 60 * 60 * 24);

            // TempLoginId 쿠키 삭제
            CookieUtils.deleteCookie(httpRequest, response, "TempLoginId");

            // 새 authorization 쿠키 설정
            CookieUtils.addCookie(response, "Authorization", token, 60 * 60 * 24);

            return ResponseEntity.ok(user);
        } catch (DuplicateNicknameException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "isSuccess", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "isSuccess", false,
                    "message", "회원가입 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
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

    @PatchMapping("/me")
    public UserUpdateResponse updateUser(
            @ModelAttribute @Valid UserUpdateRequest request,
            @RequestPart(value = "profileImageUrl", required = false) MultipartFile profileImageUrl) {

        return userService.updateUser(request, profileImageUrl);
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