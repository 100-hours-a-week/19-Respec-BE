package kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.exception;

public class ChatPartnerNotFoundException extends RuntimeException {
    public ChatPartnerNotFoundException(String message) {
        super(message);
    }

    public ChatPartnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatPartnerNotFoundException(Throwable cause) {
        super(cause);
    }
}
