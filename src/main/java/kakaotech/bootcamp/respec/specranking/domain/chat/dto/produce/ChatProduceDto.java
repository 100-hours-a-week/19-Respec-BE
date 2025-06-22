package kakaotech.bootcamp.respec.specranking.domain.chat.dto.produce;

import kakaotech.bootcamp.respec.specranking.domain.common.type.ChatStatus;

public record ChatProduceDto(
        String idempotentKey,
        Long senderId,
        Long receiverId,
        String content,
        ChatStatus status
) {
}
