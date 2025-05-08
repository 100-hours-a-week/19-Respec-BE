package kakaotech.bootcamp.respec.specranking.domain.user.service;

import kakaotech.bootcamp.respec.specranking.domain.auth.entity.OAuth;
import kakaotech.bootcamp.respec.specranking.domain.auth.repository.OAuthRepository;
import kakaotech.bootcamp.respec.specranking.domain.common.type.OAuthProvider;
import kakaotech.bootcamp.respec.specranking.domain.common.type.UserRole;
import kakaotech.bootcamp.respec.specranking.domain.common.type.UserStatus;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserResponseDto;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private static final String DEFAULT_PROFILE_URL = "https://preview.free3d.com/img/2019/02/2174901357427820287/amms1v4j.jpg";

    private final UserRepository userRepository;
    private final OAuthRepository oAuthRepository;

    // 닉네임.프로필이미지 설정 및 회원가입
    public UserResponseDto signup(UserSignupRequestDto request) {
        String loginId = request.getLoginId(); // ex. "kakao 123456789"

        if (userRepository.existsByLoginId(loginId)) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        // 닉네임 중복 검사 추가
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        // loginId 파싱
        String[] parts = loginId.split(" ", 2);
        String provider = parts[0];
        String providerId = parts[1];

        // 랜덤 비밀번호 생성
        String randomPassword = UUID.randomUUID().toString().substring(0, 16);

        // User 생성
        User user = User.builder()
                .loginId(loginId)
                .password(randomPassword)
                .nickname(request.getNickname())
                .userProfileUrl(
                        request.getUserProfileUrl() != null && !request.getUserProfileUrl().isBlank()
                                ? request.getUserProfileUrl()
                                : DEFAULT_PROFILE_URL
                )
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

    // 사용자 정보 조회
    public UserResponseDto getUserInfo(Long id) {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isEmpty()) {
            throw new RuntimeException("해당 사용자가 존재하지 않습니다.");
        }

        User user = optUser.get();

        return createUserResponseDto(user);
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

    // UserResponseDto 생성 메소드
    private UserResponseDto createUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getNickname(),
                user.getUserProfileUrl(),
                user.getCreatedAt()
        );
    }
}
