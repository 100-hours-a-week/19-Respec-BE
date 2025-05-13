package kakaotech.bootcamp.respec.specranking.domain.user.util;

public class DuplicateNicknameException extends RuntimeException {
    public DuplicateNicknameException(String message) {
        super(message);
    }
}
