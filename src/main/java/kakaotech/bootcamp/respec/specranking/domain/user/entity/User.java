package kakaotech.bootcamp.respec.specranking.domain.user.entity;

import kakaotech.bootcamp.respec.specranking.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_nickname", columnList= "nickname")
})
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
    private String userId;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String password;

    @Column(name = "user_profile_url", nullable = false, columnDefinition = "TEXT")
    private String userProfileUrl;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
    private String nickname;

    @Column(name = "is_open_spec", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isOpenSpec;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ROLE_USER'")
    private String role;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
    private String status;

    public User(String userId, String password, String userProfileUrl, String nickname, boolean isOpenSpec) {
        this.userId = userId;
        this.password = password;
        this.userProfileUrl = userProfileUrl;
        this.nickname = nickname;
        this.isOpenSpec = isOpenSpec;
    }
}
