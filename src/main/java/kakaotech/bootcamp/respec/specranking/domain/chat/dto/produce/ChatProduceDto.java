package kakaotech.bootcamp.respec.specranking.domain.chat.dto.produce;

import lombok.Data;

@Data
public class ChatProduceDto {
    private String idempotentKey;
    private String senderId;
    private String receiverId;
    private String content;
    private String status;
}
