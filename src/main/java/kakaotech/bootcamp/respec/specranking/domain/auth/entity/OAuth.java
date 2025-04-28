package kakaotech.bootcamp.respec.specranking.domain.auth.entity;

import kakaotech.bootcamp.respec.specranking.domain.common.type.OAuthProvider;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_name", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'KAKAO'")
    private OAuthProvider providerName;

    @Column(name = "provider_id", nullable = false, columnDefinition = "VARCHAR(255)")
    private String providerId;

    public OAuth(User user, OAuthProvider providerName, String providerId) {
        this.user = user;
        this.providerName = providerName;
        this.providerId = providerId;
    }
}
