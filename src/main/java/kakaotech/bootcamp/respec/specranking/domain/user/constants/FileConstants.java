package kakaotech.bootcamp.respec.specranking.domain.user.constants;

import java.util.List;

public class FileConstants {

    public static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png"
    );

    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private FileConstants() { }
}
