package kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.produce;

import kakaotech.bootcamp.respec.specranking.global.common.type.ChatStatus;

public record ChatProduceDto(
        String idempotentKey,
        Long senderId,
        Long receiverId,
        String content,
        ChatStatus status
) {
}
