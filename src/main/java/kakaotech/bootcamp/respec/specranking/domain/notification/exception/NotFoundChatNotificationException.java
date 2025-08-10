package kakaotech.bootcamp.respec.specranking.domain.notification.exception;

public class NotFoundChatNotificationException extends RuntimeException {
    public NotFoundChatNotificationException(String message) {
        super(message);
    }

    public NotFoundChatNotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundChatNotificationException(Throwable cause) {
        super(cause);
    }
}
