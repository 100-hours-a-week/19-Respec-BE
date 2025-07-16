package kakaotech.bootcamp.respec.specranking.domain.spec.spec.exception;

public class SpecNotFoundException extends RuntimeException {
    public SpecNotFoundException(String message) {
        super(message);
    }

    public SpecNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpecNotFoundException(Throwable cause) {
        super(cause);
    }
}
