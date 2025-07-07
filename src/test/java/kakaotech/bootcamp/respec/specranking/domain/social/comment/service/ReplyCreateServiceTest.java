package kakaotech.bootcamp.respec.specranking.domain.social.comment.service;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentRequest;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.ReplyPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.validator.CommentValidator;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.fixture.CommentFixture;
import kakaotech.bootcamp.respec.specranking.fixture.SpecFixture;
import kakaotech.bootcamp.respec.specranking.fixture.UserFixture;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
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
@DisplayName("대댓글 생성 서비스 테스트")
class ReplyCreateServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private SpecRepository specRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("성공: 정상적인 대댓글 생성")
    void createReply_Success() {
        // given
        CommentRequest commentRequest = new CommentRequest(REPLY_CONTENT);
        User mockUser = UserFixture.createMockUser();
        Spec mockSpec = SpecFixture.createMockSpec();
        Comment mockParentComment = CommentFixture.createMockParentComment();
        Comment mockReply = CommentFixture.createMockReply();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(specRepository.findByIdAndStatus(DEFAULT_SPEC_ID, SpecStatus.ACTIVE)).willReturn(Optional.of(mockSpec));
            given(commentRepository.findByIdAndSpecId(DEFAULT_PARENT_COMMENT_ID, DEFAULT_SPEC_ID)).willReturn(Optional.of(mockParentComment));
            willDoNothing().given(commentValidator).validateReplyCreation(mockParentComment, mockSpec);
            given(commentRepository.save(Mockito.any(Comment.class))).willReturn(mockReply);

            // when
            ReplyPostResponse response = commentService.createReply(DEFAULT_SPEC_ID, DEFAULT_PARENT_COMMENT_ID, commentRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.message()).isEqualTo(CommentMessages.REPLY_CREATE_SUCCESS);
            assertThat(response.data()).isNotNull();

            then(commentValidator).should().validateReplyCreation(mockParentComment, mockSpec);
            then(commentRepository).should().save(Mockito.any(Comment.class));
        }
    }

    @Test
    @DisplayName("실패: 부모 댓글을 찾을 수 없음")
    void createReply_ParentCommentNotFound_ThrowsException() {
        // given
        CommentRequest commentRequest = new CommentRequest(REPLY_CONTENT);
        User mockUser = UserFixture.createMockUser();
        Spec mockSpec = SpecFixture.createMockSpec();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(specRepository.findByIdAndStatus(DEFAULT_SPEC_ID, SpecStatus.ACTIVE)).willReturn(Optional.of(mockSpec));
            given(commentRepository.findByIdAndSpecId(NON_EXISTENT_COMMENT_ID, DEFAULT_SPEC_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.createReply(DEFAULT_SPEC_ID, NON_EXISTENT_COMMENT_ID, commentRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_NOT_FOUND.getMessage());

            then(commentValidator).shouldHaveNoInteractions();
        }
    }

    @Test
    @DisplayName("실패: 유효성 검증 실패 - 대댓글에 대댓글 생성 시도")
    void createReply_ReplyDepthExceeds_ThrowsException() {
        // given
        CommentRequest commentRequest = new CommentRequest(REPLY_CONTENT);
        User mockUser = UserFixture.createMockUser();
        Spec mockSpec = SpecFixture.createMockSpec();
        Comment mockNonRootComment = CommentFixture.createMockNonRootComment();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(specRepository.findByIdAndStatus(DEFAULT_SPEC_ID, SpecStatus.ACTIVE)).willReturn(Optional.of(mockSpec));
            given(commentRepository.findByIdAndSpecId(REPLY_ID, DEFAULT_SPEC_ID)).willReturn(Optional.of(mockNonRootComment));
            willThrow(new CustomException(ErrorCode.REPLY_DEPTH_EXCEEDED))
                    .given(commentValidator).validateReplyCreation(mockNonRootComment, mockSpec);

            // when & then
            assertThatThrownBy(() -> commentService.createReply(DEFAULT_SPEC_ID, REPLY_ID, commentRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.REPLY_DEPTH_EXCEEDED.getMessage());

            then(commentRepository).should().findByIdAndSpecId(REPLY_ID, DEFAULT_SPEC_ID);
            then(commentRepository).should(never()).save(Mockito.any(Comment.class));
        }
    }

    @Test
    @DisplayName("실패: 유효성 검증 실패 - 부모 댓글이 해당 스펙에 속하지 않음")
    void createReply_CommentSpecMismatch_ThrowsException() {
        // given
        CommentRequest commentRequest = new CommentRequest(REPLY_CONTENT);
        User mockUser = UserFixture.createMockUser();
        Spec mockSpec = SpecFixture.createMockSpec();
        Comment mockParentComment = CommentFixture.createMockParentComment();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(specRepository.findByIdAndStatus(DEFAULT_SPEC_ID, SpecStatus.ACTIVE)).willReturn(Optional.of(mockSpec));
            given(commentRepository.findByIdAndSpecId(REPLY_ID, DEFAULT_SPEC_ID)).willReturn(Optional.of(mockParentComment));
            willThrow(new CustomException(ErrorCode.COMMENT_SPEC_MISMATCH))
                    .given(commentValidator).validateReplyCreation(mockParentComment, mockSpec);

            // when & then
            assertThatThrownBy(() -> commentService.createReply(DEFAULT_SPEC_ID, REPLY_ID, commentRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_SPEC_MISMATCH.getMessage());

            then(commentRepository).should().findByIdAndSpecId(REPLY_ID, DEFAULT_SPEC_ID);
            then(commentRepository).should(never()).save(Mockito.any(Comment.class));
        }
    }
}
