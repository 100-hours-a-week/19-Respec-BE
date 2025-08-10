package kakaotech.bootcamp.respec.specranking.domain.user.service;

import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.constants.UserMessages;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserDetailResponse;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final SpecRepository specRepository;

    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Spec activeSpec = findActiveSpecByUserId(userId);

        UserDetailResponse.UserDetail userDetailData = UserDetailResponse.UserDetail.from(user, activeSpec);

        return UserDetailResponse.success(userDetailData, UserMessages.GET_USER_DETAIL_SUCCESS);
    }

    private Spec findActiveSpecByUserId(Long userId) {
        return specRepository.findByUserIdAndStatus(userId, SpecStatus.ACTIVE)
                .orElse(null);
    }
}
