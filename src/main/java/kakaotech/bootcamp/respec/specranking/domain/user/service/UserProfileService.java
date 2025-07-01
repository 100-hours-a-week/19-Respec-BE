package kakaotech.bootcamp.respec.specranking.domain.user.service;

import kakaotech.bootcamp.respec.specranking.domain.user.constants.UserMessages;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.*;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.validator.UserProfileValidator;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.service.ImageFileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final ImageFileStore imageFileStore;
    private final UserProfileValidator userProfileValidator;

    public UserUpdateResponse updateUserProfile(UserUpdateRequest userUpdateRequest) {
        userProfileValidator.validateUpdateRequest(userUpdateRequest);

        User user = getCurrentUser();

        if (userProfileValidator.hasNicknameUpdate(userUpdateRequest)) {
            userProfileValidator.validateNicknameDuplication(userUpdateRequest.nickname(), user.getId());
            user.updateNickname(userUpdateRequest.nickname());
        }

        if (userProfileValidator.hasProfileImageUpdate(userUpdateRequest.profileImageUrl())) {
            String newProfileImageUrl = imageFileStore.upload(userUpdateRequest.profileImageUrl());
            user.updateProfileImageUrl(newProfileImageUrl);
        }

        User savedUser = userRepository.save(user);

        return UserUpdateResponse.success(savedUser, UserMessages.USER_UPDATE_SUCCESS);
    }

    public SimpleResponseDto updateUserVisibility(boolean isPublic) {
        User user = getCurrentUser();
        user.updateIsOpenSpec(isPublic);
        userRepository.save(user);

        return SimpleResponseDto.success(UserMessages.USER_SPEC_VISIBILITY_UPDATE_SUCCESS);
    }

    private User getCurrentUser() {
        Long currentUserId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
