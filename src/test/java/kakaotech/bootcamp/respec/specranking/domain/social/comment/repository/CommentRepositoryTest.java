package kakaotech.bootcamp.respec.specranking.domain.social.comment.repository;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.fixture.RepositoryTestFixture;
import kakaotech.bootcamp.respec.specranking.global.common.config.QuerydslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@DisplayName("댓글 Repository 테스트")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private User testUser;
    private User anotherUser;
    private Spec testSpec;

    @BeforeEach
    void setUp() {
        testUser = RepositoryTestFixture.createAndSaveDefaultUser(entityManager);
        anotherUser = RepositoryTestFixture.createAndSaveAnotherUser(entityManager);
        testSpec = RepositoryTestFixture.createAndSaveDefaultSpec(entityManager, testUser);
    }

    @Nested
    @DisplayName("댓글과 대댓글 조회 테스트")
    class FindCommentsWithRepliesTest {

        @Test
        @DisplayName("성공: 삭제되지 않은 댓글과 대댓글 조회")
        void findCommentsWithReplies_WhenActiveCommentsExist_ShouldReturnCommentsWithReplies() {
            // given
            Comment parentComment = RepositoryTestFixture.createAndSaveRootComment(
                    entityManager, "부모 댓글", testUser, testSpec, INITIAL_BUNDLE_NUMBER);
            RepositoryTestFixture.createAndSaveReply(
                    entityManager, "첫 번째 대댓글", testUser, testSpec, parentComment, INITIAL_BUNDLE_NUMBER);
            RepositoryTestFixture.createAndSaveReply(
                    entityManager, "두 번째 대댓글", anotherUser, testSpec, parentComment, INITIAL_BUNDLE_NUMBER);

            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<CommentListResponse.CommentWithReplies> result = commentRepository.findCommentsWithReplies(testSpec.getId(), pageable);

            // then
            assertThat(result.getContent()).hasSize(1);

            CommentListResponse.CommentWithReplies commentWithReplies = result.getContent().getFirst();
            assertThat(commentWithReplies.commentId()).isEqualTo(parentComment.getId());
            assertThat(commentWithReplies.content()).isEqualTo("부모 댓글");
            assertThat(commentWithReplies.replyCount()).isEqualTo(2);
            assertThat(commentWithReplies.replies()).hasSize(2);

            assertThat(commentWithReplies.replies())
                    .extracting(CommentListResponse.ReplyInfo::content)
                    .containsExactly("첫 번째 대댓글", "두 번째 대댓글");
        }

        @Test
        @DisplayName("성공: 댓글이 없는 경우 빈 페이지 반환")
        void findCommentsWithReplies_WhenNoComments_ShouldReturnEmptyPage() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<CommentListResponse.CommentWithReplies> result = commentRepository.findCommentsWithReplies(testSpec.getId(), pageable);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getTotalPages()).isZero();
        }

        @Test
        @DisplayName("성공: 삭제된 댓글이지만 삭제되지 않은 대댓글이 있는 경우 조회됨")
        void findCommentsWithReplies_WhenDeletedCommentHasActiveReplies_ShouldReturnDeletedComment() {
            // given
            Comment parentComment = RepositoryTestFixture.createAndSaveRootComment(
                    entityManager, "삭제된 부모 댓글", testUser, testSpec, INITIAL_BUNDLE_NUMBER);
            RepositoryTestFixture.createAndSaveReply(
                    entityManager, "활성 대댓글", testUser, testSpec, parentComment, INITIAL_BUNDLE_NUMBER);

            parentComment.delete();
            entityManager.merge(parentComment);
            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<CommentListResponse.CommentWithReplies> result = commentRepository.findCommentsWithReplies(testSpec.getId(), pageable);

            // then
            assertThat(result.getContent()).hasSize(1);

            CommentListResponse.CommentWithReplies commentWithReplies = result.getContent().getFirst();
            assertThat(commentWithReplies.commentId()).isEqualTo(parentComment.getId());
            assertThat(commentWithReplies.replyCount()).isEqualTo(1);
            assertThat(commentWithReplies.replies()).hasSize(1);
            assertThat(commentWithReplies.replies().getFirst().content()).isEqualTo("활성 대댓글");
        }

        @Test
        @DisplayName("성공: 삭제된 댓글이고 대댓글도 없는 경우 조회되지 않음")
        void findCommentsWithReplies_WhenDeletedCommentHasNoReplies_ShouldNotReturn() {
            // given
            Comment deletedComment = RepositoryTestFixture.createAndSaveRootComment(
                    entityManager, "삭제된 댓글", testUser, testSpec, INITIAL_BUNDLE_NUMBER);
            deletedComment.delete();
            entityManager.merge(deletedComment);
            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<CommentListResponse.CommentWithReplies> result = commentRepository.findCommentsWithReplies(testSpec.getId(), pageable);

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("성공: 페이징 정보가 올바르게 반환됨")
        void findCommentsWithReplies_WhenPaging_ShouldReturnCorrectPageInfo() {
            // given
            for (int i = 1; i <= 5; i++) {
                RepositoryTestFixture.createAndSaveRootComment(entityManager, "댓글 " + i, testUser, testSpec, i);
            }
            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 3);

            // when
            Page<CommentListResponse.CommentWithReplies> result = commentRepository.findCommentsWithReplies(testSpec.getId(), pageable);

            // then
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(5);
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.isFirst()).isTrue();
            assertThat(result.isLast()).isFalse();
        }

        @Test
        @DisplayName("성공: 댓글 생성 시간 역순으로 정렬됨")
        void findCommentsWithReplies_ShouldOrderByCreatedAtDesc() {
            // given
            RepositoryTestFixture.createAndSaveRootComment(
                    entityManager, "첫 번째 댓글", testUser, testSpec, 1);
            RepositoryTestFixture.createAndSaveRootComment(
                    entityManager, "두 번째 댓글", testUser, testSpec, 2);
            RepositoryTestFixture.createAndSaveRootComment(
                    entityManager, "세 번째 댓글", testUser, testSpec, 3);

            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<CommentListResponse.CommentWithReplies> result = commentRepository.findCommentsWithReplies(testSpec.getId(), pageable);

            // then
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent())
                    .extracting(CommentListResponse.CommentWithReplies::content)
                    .containsExactly("세 번째 댓글", "두 번째 댓글", "첫 번째 댓글");
        }

        @Test
        @DisplayName("성공: 대댓글이 생성 시간 순으로 정렬됨")
        void findCommentsWithReplies_RepliesShouldOrderByCreatedAtAsc() {
            // given
            Comment parentComment = RepositoryTestFixture.createAndSaveRootComment(
                    entityManager, "부모 댓글", testUser, testSpec, INITIAL_BUNDLE_NUMBER);

            RepositoryTestFixture.createAndSaveReply(
                    entityManager, "첫 번째 대댓글", testUser, testSpec, parentComment, INITIAL_BUNDLE_NUMBER);
            RepositoryTestFixture.createAndSaveReply(
                    entityManager, "두 번째 대댓글", testUser, testSpec, parentComment, INITIAL_BUNDLE_NUMBER);
            RepositoryTestFixture.createAndSaveReply(
                    entityManager, "세 번째 대댓글", testUser, testSpec, parentComment, INITIAL_BUNDLE_NUMBER);

            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<CommentListResponse.CommentWithReplies> result = commentRepository.findCommentsWithReplies(testSpec.getId(), pageable);

            // then
            CommentListResponse.CommentWithReplies commentWithReplies = result.getContent().getFirst();

            assertThat(commentWithReplies.replies())
                    .extracting(CommentListResponse.ReplyInfo::content)
                    .containsExactly("첫 번째 대댓글", "두 번째 대댓글", "세 번째 대댓글");
        }

        @Test
        @DisplayName("성공: 다른 스펙의 댓글은 조회되지 않음")
        void findCommentsWithReplies_WhenDifferentSpec_ShouldNotReturnOtherSpecComments() {
            // given
            Spec anotherSpec = RepositoryTestFixture.createAndSaveDefaultSpec(entityManager, anotherUser);

            RepositoryTestFixture.createAndSaveRootComment(
                    entityManager, "테스트 스펙 댓글", testUser, testSpec, INITIAL_BUNDLE_NUMBER);
            RepositoryTestFixture.createAndSaveRootComment(
                    entityManager, "다른 스펙 댓글", testUser, anotherSpec, INITIAL_BUNDLE_NUMBER);

            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<CommentListResponse.CommentWithReplies> result = commentRepository.findCommentsWithReplies(testSpec.getId(), pageable);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().content()).isEqualTo("테스트 스펙 댓글");
        }
    }
}