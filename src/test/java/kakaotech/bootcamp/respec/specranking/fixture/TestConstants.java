package kakaotech.bootcamp.respec.specranking.fixture;

public class TestConstants {

    public static final Long DEFAULT_USER_ID = 1L;
    public static final String DEFAULT_USER_NICKNAME = "테스트유저";
    public static final String DEFAULT_USER_PROFILE_URL = "https://example.com/profile.jpg";

    public static final Long ANOTHER_USER_ID = 2L;
    public static final String ANOTHER_USER_NICKNAME = "다른유저";
    public static final String ANOTHER_USER_PROFILE_URL = "https://example.com/another-profile.jpg";

    public static final Long DEFAULT_SPEC_ID = 1L;
    public static final Long NON_EXISTENT_SPEC_ID = 999L;

    public static final Long DEFAULT_COMMENT_ID = 2L;
    public static final Long DEFAULT_PARENT_COMMENT_ID = 3L;
    public static final Long REPLY_ID = 4L;
    public static final Long NON_EXISTENT_COMMENT_ID = 999L;

    public static final String DEFAULT_COMMENT_CONTENT = "테스트 댓글 내용";
    public static final String REPLY_CONTENT = "대댓글 내용";
    public static final String UPDATED_COMMENT_CONTENT = "수정된 댓글 내용";
    public static final String FIRST_COMMENT_CONTENT = "첫 번째 댓글";

    public static final String DEFAULT_CREATED_AT = "2025-01-01T10:00:00";
    public static final String DEFAULT_UPDATED_AT = "2025-01-01T10:00:00";
    public static final String REPLY_CREATED_AT = "2025-01-01T11:00:00";
    public static final String REPLY_UPDATED_AT = "2025-01-01T11:00:00";

    public static final Integer INITIAL_BUNDLE_NUMBER = 1;
    public static final Integer EXISTING_MAX_BUNDLE_NUMBER = 5;

    public static final Integer ROOT_COMMENT_DEPTH = 0;
    public static final Integer REPLY_DEPTH = 1;

    private TestConstants() { }
}
