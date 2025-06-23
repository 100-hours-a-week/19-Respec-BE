package kakaotech.bootcamp.respec.specranking.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kakaotech.bootcamp.respec.specranking.global.common.entity.BaseTimeEntity;
import kakaotech.bootcamp.respec.specranking.global.common.type.UserRole;
import kakaotech.bootcamp.respec.specranking.global.common.type.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_nickname", columnList = "nickname")
        })
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
    private String loginId;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String password;

    @Column(name = "user_profile_url", nullable = false, columnDefinition = "TEXT")
    private String userProfileUrl;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
    private String nickname;

    @Column(name = "is_open_spec", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isOpenSpec;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ROLE_USER'")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
    private UserStatus status;

    public User(String loginId, String password, String userProfileUrl, String nickname, boolean isOpenSpec) {
        this.loginId = loginId;
        this.password = password;
        this.userProfileUrl = userProfileUrl;
        this.nickname = nickname;
        this.isOpenSpec = isOpenSpec;
        this.role = UserRole.ROLE_USER;
        this.status = UserStatus.ACTIVE;
    }

    public User updateNickname(String newNickname) {
        return new User(
                this.id,
                this.loginId,
                this.password,
                this.userProfileUrl,
                newNickname,
                this.isOpenSpec,
                this.role,
                this.status
        );
    }

    public User updateProfileImageUrl(String newProfileImageUrl) {
        return new User(
                this.id,
                this.loginId,
                this.password,
                newProfileImageUrl,
                this.nickname,
                this.isOpenSpec,
                this.role,
                this.status
        );
    }

    public User updateNicknameAndProfileImageUrl(String newNickname, String newProfileImageUrl) {
        return new User(
                this.id,
                this.loginId,
                this.password,
                newProfileImageUrl,
                newNickname,
                this.isOpenSpec,
                this.role,
                this.status
        );
    }

    public void updateIsOpenSpec(Boolean isOpenSpec) {
        this.isOpenSpec = isOpenSpec;
    }
}
