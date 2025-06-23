package kakaotech.bootcamp.respec.specranking.domain.auth.entity;

import kakaotech.bootcamp.respec.specranking.global.common.type.OAuthProvider;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class OAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_name", nullable = false, columnDefinition = "VARCHAR(50)")
    private OAuthProvider providerName;

    @Column(name = "provider_id", nullable = false, columnDefinition = "VARCHAR(255)")
    private String providerId;

    public OAuth(User user, OAuthProvider providerName, String providerId) {
        this.user = user;
        this.providerName = providerName;
        this.providerId = providerId;
    }
}
