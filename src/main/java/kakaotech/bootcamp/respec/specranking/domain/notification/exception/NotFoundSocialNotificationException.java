package kakaotech.bootcamp.respec.specranking.domain.notification.exception;

public class NotFoundSocialNotificationException extends RuntimeException {

    public NotFoundSocialNotificationException(String message) {
        super(message);
    }

    public NotFoundSocialNotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundSocialNotificationException(Throwable cause) {
        super(cause);
    }

}
