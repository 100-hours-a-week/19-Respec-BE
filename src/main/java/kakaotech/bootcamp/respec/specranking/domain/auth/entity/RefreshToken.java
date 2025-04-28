package kakaotech.bootcamp.respec.specranking.domain.auth.entity;

import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User user;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String value;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime expiration;

    public RefreshToken(User user, String value, LocalDateTime expiration) {
        this.user = user;
        this.value = value;
        this.expiration = expiration;
    }
}
