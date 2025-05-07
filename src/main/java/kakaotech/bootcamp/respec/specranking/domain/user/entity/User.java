package kakaotech.bootcamp.respec.specranking.domain.user.entity;

import kakaotech.bootcamp.respec.specranking.domain.common.BaseTimeEntity;
import kakaotech.bootcamp.respec.specranking.domain.common.type.UserRole;
import kakaotech.bootcamp.respec.specranking.domain.common.type.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(indexes = {
        @Index(name = "idx_nickname", columnList= "nickname")
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
    private boolean isOpenSpec;

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
}
