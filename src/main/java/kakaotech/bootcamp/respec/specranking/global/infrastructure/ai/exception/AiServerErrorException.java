package kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.exception;

public class AiServerErrorException extends RuntimeException {
    public AiServerErrorException(String message) {
        super(message);
    }

    public AiServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiServerErrorException(Throwable cause) {
        super(cause);
    }
}
