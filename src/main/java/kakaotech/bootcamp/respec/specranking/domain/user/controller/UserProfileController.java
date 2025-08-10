package kakaotech.bootcamp.respec.specranking.domain.user.controller;

import jakarta.validation.Valid;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.*;
import kakaotech.bootcamp.respec.specranking.domain.user.service.UserProfileService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PatchMapping("/me")
    public UserUpdateResponse updateUserProfile(
            @ModelAttribute @Valid UserUpdateRequest request) {
        return userProfileService.updateUserProfile(request);
    }

    @PatchMapping("/me/visibility")
    public SimpleResponseDto updateUserVisibility(@RequestBody @Valid UserVisibilityRequest request) {
        return userProfileService.updateUserVisibility(request.isPublic());
    }
}