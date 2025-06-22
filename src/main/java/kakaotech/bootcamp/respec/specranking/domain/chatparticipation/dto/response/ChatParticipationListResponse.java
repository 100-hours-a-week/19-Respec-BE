package kakaotech.bootcamp.respec.specranking.domain.chatparticipation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ChatParticipationListResponse(
        boolean isSuccess,
        String message,
        ChatParticipationListData data
) {

    public record ChatParticipationListData(
            List<ChatRoomDto> chatRooms
    ) {
        public ChatParticipationListData {
            chatRooms = List.copyOf(chatRooms); // 불변 방어 복사
        }
    }

    public record ChatRoomDto(
            Long roomId,
            Long partnerId,
            String partnerNickname,
            String partnerProfileImageUrl,
            String lastMessage,
            LocalDateTime lastMessageTime
    ) {
    }
}
