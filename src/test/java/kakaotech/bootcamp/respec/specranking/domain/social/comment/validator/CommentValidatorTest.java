package kakaotech.bootcamp.respec.specranking.domain.social.comment.validator;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.fixture.CommentFixture;
import kakaotech.bootcamp.respec.specranking.fixture.SpecFixture;
import kakaotech.bootcamp.respec.specranking.fixture.UserFixture;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 CUD 기능 검증 로직 테스트")
class CommentValidatorTest {

    @InjectMocks
    private CommentValidator commentValidator;

    @Nested
    @DisplayName("대댓글 생성 검증")
    class ValidateReplyCreation {

        @Test
        @DisplayName("성공: 정상적인 대댓글 생성 검증")
        void validateReplyCreation_WhenValid_ShouldNotThrowException() {
            // given
            Spec spec = SpecFixture.createMockSpec();
            Comment rootComment = CommentFixture.createMockComment();

            given(rootComment.belongsToSpec(spec)).willReturn(true);
            given(rootComment.isRootComment()).willReturn(true);

            // when & then
            assertThatCode(() -> commentValidator.validateReplyCreation(rootComment, spec))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 부모 댓글이 스펙에 속하지 않음")
        void validateReplyCreation_WhenCommentSpecMismatch_ShouldThrowException() {
            // given
            Spec spec = SpecFixture.createMockSpec();
            Comment rootComment = CommentFixture.createMockComment();

            given(rootComment.belongsToSpec(spec)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> commentValidator.validateReplyCreation(rootComment, spec))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_SPEC_MISMATCH.getMessage());
        }

        @Test
        @DisplayName("실패: 부모 댓글이 최상위 댓글이 아님")
        void validateReplyCreation_WhenReplyDepthExceeded_ShouldThrowException() {
            // given
            Spec spec = SpecFixture.createMockSpec();
            Comment rootComment = CommentFixture.createMockComment();

            given(rootComment.belongsToSpec(spec)).willReturn(true);
            given(rootComment.isRootComment()).willReturn(false);

            // when & then
            assertThatThrownBy(() -> commentValidator.validateReplyCreation(rootComment, spec))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.REPLY_DEPTH_EXCEEDED.getMessage());
        }
    }

    @Nested
    @DisplayName("댓글 수정/삭제 검증")
    class ValidateCommentUpdateAndDeletion {

        @Test
        @DisplayName("성공: 정상적인 댓글 수정/삭제 검증")
        void validateCommentUpdateAndDeletion_WhenValid_ShouldNotThrowException() {
            // given
            User user = UserFixture.createMockUser();
            Comment comment = CommentFixture.createMockComment();

            given(comment.isWrittenBy(user)).willReturn(true);
            given(comment.isDeleted()).willReturn(false);

            // when & then
            assertThatCode(() -> commentValidator.validateCommentUpdate(comment, user))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 댓글 수정/삭제 권한 없음")
        void validateCommentUpdateAndDeletion_WhenCommentAccessDenied_ShouldThrowException() {
            // given
            User user = UserFixture.createMockUser();
            Comment comment = CommentFixture.createMockComment();

            given(comment.isWrittenBy(user)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> commentValidator.validateCommentUpdate(comment, user))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_ACCESS_DENIED.getMessage());
        }

        @Test
        @DisplayName("실패: 이미 삭제된 댓글을 수정/삭제 시도함")
        void validateCommentUpdateAndDeletion_WhenCommentAlreadyDeleted_ShouldThrowException() {
            // given
            User user = UserFixture.createMockUser();
            Comment comment = CommentFixture.createMockComment();

            given(comment.isWrittenBy(user)).willReturn(true);
            given(comment.isDeleted()).willReturn(true);

            // when & then
            assertThatThrownBy(() -> commentValidator.validateCommentUpdate(comment, user))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_ALREADY_DELETED.getMessage());
        }
    }
}