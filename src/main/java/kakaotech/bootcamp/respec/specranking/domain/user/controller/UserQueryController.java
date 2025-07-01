package kakaotech.bootcamp.respec.specranking.domain.user.controller;

import jakarta.validation.constraints.Positive;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserDetailResponse;
import kakaotech.bootcamp.respec.specranking.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;

    @GetMapping("/{userId}")
    public UserDetailResponse getUserDetail(
            @PathVariable
            @Positive(message = "userId는 양수여야 합니다.")
            Long userId) {
        return userQueryService.getUserDetail(userId);
    }
}
