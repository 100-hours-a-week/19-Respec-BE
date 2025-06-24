package kakaotech.bootcamp.respec.specranking.domain.user.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import kakaotech.bootcamp.respec.specranking.domain.auth.entity.OAuth;
import kakaotech.bootcamp.respec.specranking.domain.auth.repository.OAuthRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserDetailResponse;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserUpdateRequest;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserUpdateResponse;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.exception.DuplicateNicknameException;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.global.common.type.OAuthProvider;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.global.common.type.UserRole;
import kakaotech.bootcamp.respec.specranking.global.common.type.UserStatus;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.service.ImageFileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OAuthRepository oAuthRepository;
    private final SpecRepository specRepository;
    private final ImageFileStore imageFileStore;

    // 닉네임.프로필이미지 설정 및 회원가입
    public UserResponseDto signup(UserSignupRequestDto request, MultipartFile profileImage) {
        String loginId = request.getLoginId(); // ex. "kakao 123456789"

        if (userRepository.existsByLoginId(loginId)) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        String nickname = request.getNickname();

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
        }

        // loginId 파싱
        String[] parts = loginId.split(" ", 2);
        String provider = parts[0];
        String providerId = parts[1];

        // 랜덤 비밀번호 생성
        String randomPassword = UUID.randomUUID().toString().substring(0, 16);

        // 프로필 이미지 처리
        String profileUrl;
        if (profileImage != null) {
            try {
                profileUrl = imageFileStore.upload(profileImage);
            } catch (Exception e) {
                throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
            }
        } else {
            // S3에서 기본 이미지 URL 가져오기
            profileUrl = imageFileStore.getDefaultImageUrl();
        }

        // User 생성
        User user = User.builder()
                .loginId(loginId)
                .password(randomPassword)
                .nickname(nickname)
                .userProfileUrl(profileUrl)
                .isOpenSpec(true)
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        // OAuth 엔티티 생성
        OAuth oAuth = OAuth.builder()
                .providerId(providerId)
                .providerName(OAuthProvider.valueOf(provider))
                .user(user)
                .build();
        oAuthRepository.save(oAuth);

        return createUserResponseDto(user);
    }

    public UserDetailResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Optional<Spec> activeSpecOpt = specRepository.findByUserIdAndStatus(user.getId(), SpecStatus.ACTIVE);
        Spec activeSpec = activeSpecOpt.orElse(null);

        UserDetailResponse.UserDetail userDetail = UserDetailResponse.createUserDetail(user, activeSpec);

        return UserDetailResponse.success(userDetail);
    }

    // 회원 탈퇴
    public void deleteUser(Long id) {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isEmpty()) {
            throw new RuntimeException("해당 사용자가 존재하지 않습니다.");
        }

        User user = optUser.get();
        user.setDeletedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public UserUpdateResponse updateUser(UserUpdateRequest request, MultipartFile profileImageUrl) {
        Optional<Long> optUserId = UserUtils.getCurrentUserId();
        Long userId = optUserId.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        boolean hasNicknameUpdate = request != null && request.hasNickname();
        boolean hasProfileImageUpdate = profileImageUrl != null && !profileImageUrl.isEmpty();

        if (!hasNicknameUpdate && !hasProfileImageUpdate) {
            throw new IllegalArgumentException("수정할 정보가 없습니다.");
        }

        if (hasNicknameUpdate) {
            validateNicknameDuplication(request.getNickname(), userId);
        }

        String newProfileImageUrl = null;
        if (hasProfileImageUpdate) {
            newProfileImageUrl = imageFileStore.upload(profileImageUrl);
        }

        User updatedUser = updateUserEntity(user, hasNicknameUpdate ? request.getNickname() : null, newProfileImageUrl);

        User savedUser = userRepository.save(updatedUser);
        return UserUpdateResponse.success(savedUser);
    }

    public void updateUserVisibility(Boolean isPublic) {
        Optional<Long> optUserId = UserUtils.getCurrentUserId();
        Long userId = optUserId.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        user.updateIsOpenSpec(isPublic);
    }

    // UserResponseDto 생성 메소드
    private UserResponseDto createUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getNickname(),
                user.getUserProfileUrl(),
                user.getCreatedAt()
        );
    }

    private void validateNicknameDuplication(String nickname, Long currentUserId) {
        if (userRepository.existsByNicknameAndIdNot(nickname, currentUserId)) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
        }
    }

    private User updateUserEntity(User user, String newNickname, String newProfileImageUrl) {
        if (newNickname != null && newProfileImageUrl != null) {
            return user.updateNicknameAndProfileImageUrl(newNickname, newProfileImageUrl);
        } else if (newNickname != null) {
            return user.updateNickname(newNickname);
        } else if (newProfileImageUrl != null) {
            return user.updateProfileImageUrl(newProfileImageUrl);
        } else {
            return user;
        }
    }
}
