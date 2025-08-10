package kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.response;

import java.util.List;

public record ChatListResponse(
        Boolean isSuccess,
        String message,
        ChatListData data
) {
    public record ChatListData(
            Long partnerId,
            List<ChatMessageDto> messages,
            Boolean hasNext,
            String nextCursor
    ) {
        public ChatListData {
            messages = List.copyOf(messages);
        }
    }

    public record ChatMessageDto(
            Long messageId,
            Long senderId,
            String content,
            String createdAt
    ) {
    }
}
