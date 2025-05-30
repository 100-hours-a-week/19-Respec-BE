package kakaotech.bootcamp.respec.specranking.domain.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.CookieUtils;
import kakaotech.bootcamp.respec.specranking.domain.auth.service.AuthService;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.user.service.UserService;
import kakaotech.bootcamp.respec.specranking.domain.user.util.DuplicateNicknameException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String TEMP_LOGIN_ID = "TempLoginId";
    private static final String ACCESS = "access";
    private static final Long ACCESS_EXP = 1000L * 60; // 1분

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserSignupResponseDto createUser(
            @RequestPart("nickname") String nickname,
            @RequestPart("loginId") String loginId,
            @RequestPart(value = "userProfileUrl", required = false) MultipartFile profileImage,
            HttpServletResponse response) {

        if (loginId == null) {
            return UserSignupResponseDto.fail("loginId가 필요합니다.");
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String contentType = profileImage.getContentType();

            if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
                return UserSignupResponseDto.fail("PNG 또는 JPG 형식의 이미지만 업로드 가능합니다.");
            }

            if (profileImage.getSize() > MAX_FILE_SIZE) {
                return UserSignupResponseDto.fail("이미지 크기는 10MB 이하여야 합니다.");
            }
        }

        try {
            UserSignupRequestDto signupRequestDto = new UserSignupRequestDto(loginId, nickname, null);
            UserResponseDto user = userService.signup(signupRequestDto, profileImage);

            CookieUtils.deleteCookie(response, TEMP_LOGIN_ID);

            AuthTokenRequestDto requestDto = new AuthTokenRequestDto(user.getId(), signupRequestDto.getLoginId());
            AuthTokenResponseDto responseDto = authService.issueToken(requestDto, false);
            authService.convertTokenToResponse(responseDto, response);

            CookieUtils.addCookie(response, ACCESS, responseDto.accessToken(), (int) (ACCESS_EXP / 1000));

            return UserSignupResponseDto.success(user);
        } catch (DuplicateNicknameException e) {
            return UserSignupResponseDto.fail(e.getMessage());
        } catch (Exception e) {
            return UserSignupResponseDto.fail("회원가입 중 오류가 발생했습니다: " + e.getMessage());
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
}