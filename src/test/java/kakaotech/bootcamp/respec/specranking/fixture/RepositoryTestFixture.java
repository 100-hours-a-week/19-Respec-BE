package kakaotech.bootcamp.respec.specranking.fixture;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;

public class RepositoryTestFixture {

    public static User createAndSaveUser(TestEntityManager entityManager, String loginId, String nickname) {
        User user = new User(
                loginId,
                "encoded_password_" + loginId,
                nickname,
                "https://example.com/profile/" + loginId + ".jpg",
                true
        );
        return entityManager.persistAndFlush(user);
    }

    public static User createAndSaveDefaultUser(TestEntityManager entityManager) {
        return createAndSaveUser(entityManager, DEFAULT_USER_LOGIN_ID, DEFAULT_USER_NICKNAME);
    }

    public static User createAndSaveAnotherUser(TestEntityManager entityManager) {
        return createAndSaveUser(entityManager, ANOTHER_USER_LOGIN_ID, ANOTHER_USER_NICKNAME);
    }

    public static Spec createAndSaveSpec(TestEntityManager entityManager, User user, String assessment) {
        Spec spec = new Spec(
                user,
                JobField.INTERNET_IT,
                DEFAULT_EDUCATION_SCORE,
                DEFAULT_WORK_EXPERIENCE_SCORE,
                DEFAULT_ACTIVITY_NETWORKING_SCORE,
                DEFAULT_CERTIFICATION_SCORE,
                DEFAULT_ENGLISH_SKILL_SCORE,
                DEFAULT_TOTAL_ANALYSIS_SCORE,
                assessment
        );
        return entityManager.persistAndFlush(spec);
    }

    public static Spec createAndSaveDefaultSpec(TestEntityManager entityManager, User user) {
        return createAndSaveSpec(entityManager, user, DEFAULT_ASSESSMENT);
    }

    public static Comment createAndSaveRootComment(TestEntityManager entityManager,
                                                   String content, User writer, Spec spec, int bundle) {
        Comment comment = new Comment(
                spec,
                writer,
                null,
                content,
                bundle,
                ROOT_COMMENT_DEPTH
        );
        return entityManager.persistAndFlush(comment);
    }

    public static Comment createAndSaveReply(TestEntityManager entityManager, String content,
                                             User writer, Spec spec, Comment parentComment, int bundle) {
        Comment reply = new Comment(
                spec,
                writer,
                parentComment,
                content,
                bundle,
                REPLY_DEPTH
        );
        return entityManager.persistAndFlush(reply);
    }

    private RepositoryTestFixture() { }
}
