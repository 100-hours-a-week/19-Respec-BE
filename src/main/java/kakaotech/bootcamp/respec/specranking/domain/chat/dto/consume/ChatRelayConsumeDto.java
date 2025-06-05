package kakaotech.bootcamp.respec.specranking.domain.chat.dto.consume;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRelayConsumeDto {
    private Long senderId;
    private Long receiverId;
    private String content;

}
