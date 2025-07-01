package kakaotech.bootcamp.respec.specranking.domain.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Positive;
import kakaotech.bootcamp.respec.specranking.domain.auth.cookie.CookieUtils;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenResponseDto;
import jakarta.validation.Valid;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import kakaotech.bootcamp.respec.specranking.domain.auth.service.AuthService;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.*;
import kakaotech.bootcamp.respec.specranking.domain.user.service.UserService;
import kakaotech.bootcamp.respec.specranking.global.exception.DuplicateNicknameException;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final CookieUtils cookieUtils;

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

            cookieUtils.deleteCookie(response, TEMP_LOGIN_ID);

            AuthTokenRequestDto requestDto = new AuthTokenRequestDto(user.getId(), signupRequestDto.getLoginId());
            AuthTokenResponseDto responseDto = authService.issueToken(requestDto, false);
            authService.convertTokenToResponse(responseDto, response);

            cookieUtils.addCookie(response, ACCESS, responseDto.accessToken(), (int) (ACCESS_EXP / 1000));

            return UserSignupResponseDto.success(user);
        } catch (DuplicateNicknameException e) {
            throw e;
        } catch (Exception e) {
            return UserSignupResponseDto.fail("회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public UserDetailResponse getUserInfo(
            @PathVariable
            @Positive(message = "userId는 양수여야 합니다.")
            Long userId) {
        return userService.getUserInfo(userId);
    }

    @PatchMapping("/me")
    public UserUpdateResponse updateUserProfile(
            @ModelAttribute @Valid UserUpdateRequest request,
            @RequestPart(value = "profileImageUrl", required = false) MultipartFile profileImageUrl) {
        return userService.updateUserProfile(request, profileImageUrl);
    }

    @PatchMapping("/me/visibility")
    public SimpleResponseDto updateUserVisibility(@RequestBody @Valid UserVisibilityRequest request) {
        return userService.updateUserVisibility(request.isPublic());
    }

    @DeleteMapping("/me")
    public SimpleResponseDto deleteUser(@AuthenticationPrincipal AuthenticatedUserDto userDto) {
        return userService.deleteUser(userDto.getId());
    }
}