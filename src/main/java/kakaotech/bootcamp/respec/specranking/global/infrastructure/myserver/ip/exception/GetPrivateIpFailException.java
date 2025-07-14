package kakaotech.bootcamp.respec.specranking.global.infrastructure.myserver.ip.exception;

public class GetPrivateIpFailException extends RuntimeException {
    public GetPrivateIpFailException(String message) {
        super(message);
    }

    public GetPrivateIpFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetPrivateIpFailException(Throwable cause) {
        super(cause);
    }
}
