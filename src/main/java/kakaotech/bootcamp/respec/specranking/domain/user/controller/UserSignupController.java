package kakaotech.bootcamp.respec.specranking.domain.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupRequest;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupResponse;
import kakaotech.bootcamp.respec.specranking.domain.user.service.UserSignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserSignupController {

    private final UserSignupService userSignupService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserSignupResponse signup(
            @ModelAttribute @Valid UserSignupRequest request,
            HttpServletResponse response) {
        return userSignupService.signup(request, response);
    }
}
