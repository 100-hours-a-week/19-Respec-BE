package kakaotech.bootcamp.respec.specranking.domain.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationStatusResponse {
    private boolean isSuccess;
    private String message;
    private NotificationStatusData data;

    @Getter
    @AllArgsConstructor
    public static class NotificationStatusData {
        private boolean hasUnreadChat;
        private boolean hasUnreadComment;
    }
}
