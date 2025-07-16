package kakaotech.bootcamp.respec.specranking.domain.spec.spec.exception;

public class SpecUpdateForbiddenException extends RuntimeException {
    public SpecUpdateForbiddenException(String message) {
        super(message);
    }

    public SpecUpdateForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpecUpdateForbiddenException(Throwable cause) {
        super(cause);
    }
}
