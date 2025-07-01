package kakaotech.bootcamp.respec.specranking.domain.user.validator;

import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserUpdateRequest;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class UserProfileValidator {

    private final UserRepository userRepository;
    private final UserCommonValidator userCommonValidator;

    public void validateUpdateRequest(UserUpdateRequest request) {
        validateHasUpdateData(request);
        userCommonValidator.validateProfileImageFile(request.profileImageUrl());
    }

    public void validateNicknameDuplication(String nickname, Long currentUserId) {
        if (userRepository.existsByNicknameAndIdNot(nickname, currentUserId)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public boolean hasNicknameUpdate(UserUpdateRequest request) {
        return (request != null && !request.nickname().isEmpty());
    }

    public boolean hasProfileImageUpdate(MultipartFile profileImageUrl) {
        return (profileImageUrl != null && !profileImageUrl.isEmpty());
    }

    private void validateHasUpdateData(UserUpdateRequest request) {
        if (!hasNicknameUpdate(request) && !hasProfileImageUpdate(request.profileImageUrl())) {
            throw new CustomException(ErrorCode.NO_USER_DATA_TO_UPDATE);
        }
    }
}
