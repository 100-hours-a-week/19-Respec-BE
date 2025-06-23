package kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.request;

public record SocketChatSendRequest(
        Long receiverId,
        String content
) {
}
