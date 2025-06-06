package kakaotech.bootcamp.respec.specranking.domain.chatparticipation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatParticipationListResponse {
    private boolean isSuccess;
    private String message;
    private ChatParticipationListData data;

    @Getter
    @Builder
    public static class ChatParticipationListData {
        private List<ChatRoomDto> chatRooms;
    }

    @Getter
    @AllArgsConstructor
    public static class ChatRoomDto {
        private Long roomId;
        private Long partnerId;
        private String partnerNickname;
        private String partnerProfileImageUrl;
        private String lastMessage;
        private LocalDateTime lastMessageTime;
    }
}
