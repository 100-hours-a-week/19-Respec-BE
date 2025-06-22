package kakaotech.bootcamp.respec.specranking.domain.chat.dto.request;

public record SocketChatSendRequest(
        Long receiverId,
        String content
) {
}
