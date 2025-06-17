package kakaotech.bootcamp.respec.specranking.domain.chat.dto.request;

import lombok.Getter;

@Getter
public class SocketChatSendRequest {
    private final Long receiverId;
    private final String content;

    public SocketChatSendRequest(Long receiverId, String content) {
        this.receiverId = receiverId;
        this.content = content;
    }
}
