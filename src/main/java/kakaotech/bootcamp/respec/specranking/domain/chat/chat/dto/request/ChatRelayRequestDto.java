package kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.request;

public record ChatRelayRequestDto(
        Long senderId,
        Long receiverId,
        String content
) {
}
