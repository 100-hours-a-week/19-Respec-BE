package kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.consume;

public record ChatRelayConsumeDto(
        Long senderId,
        Long receiverId,
        String content
) {
}
