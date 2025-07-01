package kakaotech.bootcamp.respec.specranking.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kakaotech.bootcamp.respec.specranking.domain.user.constants.ValidationConstants;
import org.springframework.web.multipart.MultipartFile;

public record UserSignupRequest (
    @NotBlank(message = "loginId는 필수입니다.")
    String loginId,

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = ValidationConstants.NICKNAME_MIN_LENGTH,
          max = ValidationConstants.NICKNAME_MAX_LENGTH,
          message = "닉네임은 2자 이상 11자 이하여야 합니다.")
    @Pattern(regexp = ValidationConstants.NICKNAME_PATTERN,
             message = "닉네임은 영문, 숫자, 한글만 사용 가능합니다.")
    String nickname,

    MultipartFile profileImageUrl
) { }
