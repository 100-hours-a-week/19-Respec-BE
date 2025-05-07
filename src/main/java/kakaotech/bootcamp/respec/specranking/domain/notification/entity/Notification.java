package kakaotech.bootcamp.respec.specranking.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kakaotech.bootcamp.respec.specranking.domain.common.type.NotificationTargetType;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "target_name", nullable = false, columnDefinition = "VARCHAR(50)")
    private NotificationTargetType targetName;

    @Column(name = "target_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long targetId;

    public Notification(User user, NotificationTargetType targetName, Long targetId) {
        this.user = user;
        this.targetName = targetName;
        this.targetId = targetId;
    }
}
