package kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.response;

public record ChatRelayResponse(
        Long senderId,
        Long receiverId,
        String content
) {
}
