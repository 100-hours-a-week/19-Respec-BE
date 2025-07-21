package kakaotech.bootcamp.respec.specranking.domain.spec.spec.exception;

public class SpecDuplicateTryException extends RuntimeException {
    public SpecDuplicateTryException(String message) {
        super(message);
    }

    public SpecDuplicateTryException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpecDuplicateTryException(Throwable cause) {
        super(cause);
    }
}
