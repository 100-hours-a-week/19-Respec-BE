package kakaotech.bootcamp.respec.specranking.domain.social.comment.service;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentRequest;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentUpdateResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.validator.CommentValidator;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.fixture.CommentFixture;
import kakaotech.bootcamp.respec.specranking.fixture.UserFixture;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 수정 서비스 테스트")
public class CommentUpdateServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentService commentService;

    //=========================================================================================
    // Common Failure Tests : COMMENT_NOT_FOUND, COMMENT_ACCESS_DENIED, COMMENT_ALREADY_DELETED
    //=========================================================================================

    @Test
    @DisplayName("공통 실패: 댓글을 찾을 수 없음")
    void updateComment_CommentNotFound_ThrowsException() {
        // given
        CommentRequest commentRequest = new CommentRequest(UPDATED_COMMENT_CONTENT);
        User mockUser = UserFixture.createMockUser();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(commentRepository.findByIdAndSpecId(NON_EXISTENT_COMMENT_ID, DEFAULT_SPEC_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.updateComment(DEFAULT_SPEC_ID, NON_EXISTENT_COMMENT_ID, commentRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_NOT_FOUND.getMessage());

            then(commentValidator).shouldHaveNoInteractions();
        }
    }

    @Test
    @DisplayName("공통 실패: 유효성 검증 실패 - 댓글 수정/삭제 권한 없음")
    void updateComment_NoPermission_ThrowsException() {
        // given
        CommentRequest commentRequest = new CommentRequest(UPDATED_COMMENT_CONTENT);
        User mockUser = UserFixture.createMockUser();
        Comment mockComment = CommentFixture.createMockComment();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(commentRepository.findByIdAndSpecId(DEFAULT_COMMENT_ID, DEFAULT_SPEC_ID)).willReturn(Optional.of(mockComment));
            willThrow(new CustomException(ErrorCode.COMMENT_ACCESS_DENIED))
                    .given(commentValidator).validateCommentUpdate(mockComment, mockUser);

            // when & then
            assertThatThrownBy(() -> commentService.updateComment(DEFAULT_SPEC_ID, DEFAULT_COMMENT_ID, commentRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_ACCESS_DENIED.getMessage());

            then(commentRepository).should(never()).save(Mockito.any(Comment.class));
        }
    }

    @Test
    @DisplayName("공통 실패: 유효성 검증 실패 - 이미 삭제된 댓글 수정/삭제 시도")
    void updateComment_commentAlreadyDeleted_ThrowsException() {
        // given
        CommentRequest commentRequest = new CommentRequest(UPDATED_COMMENT_CONTENT);
        User mockUser = UserFixture.createMockUser();
        Comment mockComment = CommentFixture.createMockComment();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(commentRepository.findByIdAndSpecId(DEFAULT_COMMENT_ID, DEFAULT_SPEC_ID)).willReturn(Optional.of(mockComment));
            willThrow(new CustomException(ErrorCode.COMMENT_ALREADY_DELETED))
                    .given(commentValidator).validateCommentUpdate(mockComment, mockUser);

            // when & then
            assertThatThrownBy(() -> commentService.updateComment(DEFAULT_SPEC_ID, DEFAULT_COMMENT_ID, commentRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_ALREADY_DELETED.getMessage());

            then(commentRepository).should(never()).save(Mockito.any(Comment.class));
        }
    }

    @Test
    @DisplayName("성공: 정상적인 댓글 수정")
    void updateComment_Success() {
        // given
        CommentRequest commentRequest = new CommentRequest(UPDATED_COMMENT_CONTENT);
        User mockUser = UserFixture.createMockUser();
        Comment mockComment = CommentFixture.createMockComment();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(commentRepository.findByIdAndSpecId(DEFAULT_COMMENT_ID, DEFAULT_SPEC_ID)).willReturn(Optional.of(mockComment));
            willDoNothing().given(commentValidator).validateCommentUpdate(mockComment, mockUser);
            given(commentRepository.save(mockComment)).willReturn(mockComment);

            // when
            CommentUpdateResponse response = commentService.updateComment(DEFAULT_SPEC_ID, DEFAULT_COMMENT_ID, commentRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.message()).isEqualTo(CommentMessages.COMMENT_UPDATE_SUCCESS);
            assertThat(response.data()).isNotNull();

            then(commentValidator).should().validateCommentUpdate(mockComment, mockUser);
            then(commentRepository).should().save(mockComment);
        }
    }
}
