package kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.exception;

public class S3RemoveFileFailException extends RuntimeException {
    public S3RemoveFileFailException(String message) {
        super(message);
    }

    public S3RemoveFileFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public S3RemoveFileFailException(Throwable cause) {
        super(cause);
    }
}
