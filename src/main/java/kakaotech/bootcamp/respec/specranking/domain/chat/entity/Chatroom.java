package kakaotech.bootcamp.respec.specranking.domain.chat.entity;

import kakaotech.bootcamp.respec.specranking.domain.common.BaseTimeEntity;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chatroom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User partner;

    @Column(name = "creator_delete_at", columnDefinition = "TIMESTAMP NULL")
    private LocalDateTime creatorDeleteAt;

    @Column(name = "partner_delete_at", columnDefinition = "TIMESTAMP NULL")
    private LocalDateTime partnerDeleteAt;

    public Chatroom(User creator, User partner) {
        this.creator = creator;
        this.partner = partner;
    }

    public void deleteCreator() {
        this.creatorDeleteAt = LocalDateTime.now();
    }

    public void deletePartner() {
        this.partnerDeleteAt = LocalDateTime.now();
    }
}
