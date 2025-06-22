package kakaotech.bootcamp.respec.specranking.domain.chat.dto.response;

import java.util.List;

public record ChatListResponse(
        boolean isSuccess,
        String message,
        ChatListData data
) {
    public record ChatListData(
            Long partnerId,
            List<ChatMessageDto> messages,
            boolean hasNext,
            String nextCursor
    ) {
    }

    public record ChatMessageDto(
            Long messageId,
            Long senderId,
            String content,
            String createdAt
    ) {
    }
}
