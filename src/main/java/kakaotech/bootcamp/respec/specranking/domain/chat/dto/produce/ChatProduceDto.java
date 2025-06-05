package kakaotech.bootcamp.respec.specranking.domain.chat.dto.produce;

import lombok.Getter;

@Getter
public class ChatProduceDto {
    private String idempotentKey;
    private String senderId;
    private String receiverId;
    private String content;
    private String status;

    public ChatProduceDto(String idempotentKey, String senderId, String receiverId, String content, String status) {
        this.idempotentKey = idempotentKey;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.status = status;
    }
}
