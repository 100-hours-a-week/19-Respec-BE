package kakaotech.bootcamp.respec.specranking.domain.chat.dto.consume;

public record ChatRelayConsumeDto(
        Long senderId,
        Long receiverId,
        String content
) {
}
