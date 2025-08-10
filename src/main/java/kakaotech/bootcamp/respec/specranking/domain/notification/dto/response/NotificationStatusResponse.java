package kakaotech.bootcamp.respec.specranking.domain.notification.dto.response;

public record NotificationStatusResponse(
        boolean isSuccess,
        String message,
        NotificationStatusData data
) {

    public record NotificationStatusData(
            boolean hasUnreadChat,
            boolean hasUnreadComment
    ) {
    }
}
