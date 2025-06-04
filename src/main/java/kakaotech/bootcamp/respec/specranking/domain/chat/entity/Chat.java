package kakaotech.bootcamp.respec.specranking.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kakaotech.bootcamp.respec.specranking.domain.chatroom.entity.Chatroom;
import kakaotech.bootcamp.respec.specranking.domain.common.BaseTimeEntity;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "idx_chatroom_create_at", columnList = "chatroom_id, create_at")
        }
)
public class Chat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Chatroom chatroom;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String content;

    public Chat(User sender, User receiver, Chatroom chatroom, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.chatroom = chatroom;
        this.content = content;
    }
}
