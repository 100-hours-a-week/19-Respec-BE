package kakaotech.bootcamp.respec.specranking.domain.notification.entity;

import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User user;

    @Column(name = "target_name", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'CHATTING'")
    private String targetName;

    @Column(name = "target_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long targetId;

    public Notification(User user, String targetName, Long targetId) {
        this.user = user;
        this.targetName = targetName;
        this.targetId = targetId;
    }
}
