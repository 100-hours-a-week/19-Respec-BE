package kakaotech.bootcamp.respec.specranking.domain.chat.dto.produce;

import kakaotech.bootcamp.respec.specranking.domain.common.type.ChatStatus;
import lombok.Getter;

@Getter
public class ChatProduceDto {
    private String idempotentKey;
    private Long senderId;
    private Long receiverId;
    private String content;
    private ChatStatus status;

    public ChatProduceDto(String idempotentKey, Long senderId, Long receiverId, String content, ChatStatus status) {
        this.idempotentKey = idempotentKey;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.status = status;
    }
}
