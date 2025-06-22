package kakaotech.bootcamp.respec.specranking.domain.chat.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatListResponse {
    private Boolean isSuccess;
    private String message;
    private ChatListData data;

    @Getter
    @Builder
    public static class ChatListData {
        private Long partnerId;
        private List<ChatMessageDto> messages;
        private Boolean hasNext;
        private String nextCursor;
    }

    @Getter
    @AllArgsConstructor
    public static class ChatMessageDto {
        private Long messageId;
        private Long senderId;
        private String content;
        private String createdAt;
    }
}
