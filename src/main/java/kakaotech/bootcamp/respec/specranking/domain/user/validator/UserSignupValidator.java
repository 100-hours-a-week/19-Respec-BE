package kakaotech.bootcamp.respec.specranking.domain.user.validator;

import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupRequest;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSignupValidator {

    private final UserRepository userRepository;
    private final UserCommonValidator userCommonValidator;

    public void validateSignupRequest(UserSignupRequest userSignupRequest) {
        userCommonValidator.validateProfileImageFile(userSignupRequest.profileImageUrl());
        validateUserNotExists(userSignupRequest.loginId());
        validateNicknameDuplication(userSignupRequest.nickname());
    }

    private void validateUserNotExists(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    private void validateNicknameDuplication(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
