package kakaotech.bootcamp.respec.specranking.domain.user.constants;

public class ValidationConstants {

    public static final int NICKNAME_MIN_LENGTH = 2;
    public static final int NICKNAME_MAX_LENGTH = 11;
    public static final String NICKNAME_PATTERN = "^[a-zA-Z0-9가-힣]*$";

    private ValidationConstants() { }
}
