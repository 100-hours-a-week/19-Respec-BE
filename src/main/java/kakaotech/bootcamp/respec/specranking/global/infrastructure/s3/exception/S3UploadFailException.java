package kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.exception;

public class S3UploadFailException extends RuntimeException {
    public S3UploadFailException(String message) {
        super(message);
    }

    public S3UploadFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public S3UploadFailException(Throwable cause) {
        super(cause);
    }
}
