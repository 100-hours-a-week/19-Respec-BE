package kakaotech.bootcamp.respec.specranking.domain.spec.spec.exception;

public class SpecNotAbleUpdateException extends RuntimeException {
    public SpecNotAbleUpdateException(String message) {
        super(message);
    }

    public SpecNotAbleUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpecNotAbleUpdateException(Throwable cause) {
        super(cause);
    }
}
