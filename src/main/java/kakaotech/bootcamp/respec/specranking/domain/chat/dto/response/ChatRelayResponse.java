package kakaotech.bootcamp.respec.specranking.domain.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRelayResponse {
    private Long senderId;
    private Long receiverId;
    private String content;

}
