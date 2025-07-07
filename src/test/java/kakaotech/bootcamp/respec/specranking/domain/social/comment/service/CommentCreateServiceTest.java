package kakaotech.bootcamp.respec.specranking.domain.social.comment.service;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentRequest;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 생성 서비스 테스트")
class CommentCreateServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private SpecRepository specRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    //====================================================================
    // Common Failure Tests : UNAUTHORIZED, USER_NOT_FOUND, SPEC_NOT_FOUND
    //====================================================================

    @Test
    @DisplayName("공통 실패: 사용자가 인증되지 않음")
    void createComment_Unauthorized_ThrowsException() {
        // given
        CommentRequest commentRequest = new CommentRequest(DEFAULT_COMMENT_CONTENT);

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupUnauthenticatedUser(mockUserUtils);

            // when & then
            assertThatThrownBy(() -> commentService.createComment(DEFAULT_SPEC_ID, commentRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.UNAUTHORIZED.getMessage());

            then(commentRepository).shouldHaveNoInteractions();
        }
    }

    @Test
    @DisplayName("공통 실패: 사용자를 찾을 수 없음")
    void createComment_UserNotFound_ThrowsException() {
        // given
        CommentRequest commentRequest = new CommentRequest(DEFAULT_COMMENT_CONTENT);

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);
            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.createComment(DEFAULT_SPEC_ID, commentRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    @Test
    @DisplayName("공통 실패: 스펙을 찾을 수 없음")
    void createComment_SpecNotFound_ThrowsException() {
        // given
        CommentRequest commentRequest = new CommentRequest(DEFAULT_COMMENT_CONTENT);
        User mockUser = UserFixture.createMockUser();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);
            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(specRepository.findByIdAndStatus(NON_EXISTENT_SPEC_ID, SpecStatus.ACTIVE)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.createComment(NON_EXISTENT_SPEC_ID, commentRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.SPEC_NOT_FOUND.getMessage());
        }
    }

    @Test
    @DisplayName("성공: 정상적인 댓글 생성")
    void createComment_Success() {
        // given
        CommentRequest commentRequest = new CommentRequest(DEFAULT_COMMENT_CONTENT);
        User mockUser = UserFixture.createMockUser();
        Spec mockSpec = SpecFixture.createMockSpec();
        Comment mockComment = CommentFixture.createMockComment();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(specRepository.findByIdAndStatus(DEFAULT_SPEC_ID, SpecStatus.ACTIVE)).willReturn(Optional.of(mockSpec));
            given(commentRepository.findMaxBundleBySpecId(DEFAULT_SPEC_ID)).willReturn(EXISTING_MAX_BUNDLE_NUMBER);
            given(commentRepository.save(Mockito.any(Comment.class))).willReturn(mockComment);

            // when
            CommentPostResponse response = commentService.createComment(DEFAULT_SPEC_ID, commentRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.message()).isEqualTo(CommentMessages.COMMENT_CREATE_SUCCESS);
            assertThat(response.data()).isNotNull();

            then(commentRepository).should().save(Mockito.any(Comment.class));
        }
    }

    @Test
    @DisplayName("성공: 첫 번째 댓글 생성 (번들 번호 1)")
    void createComment_FirstComment_BundleNumberOne() {
        // given
        CommentRequest commentRequest = new CommentRequest(FIRST_COMMENT_CONTENT);
        User mockUser = UserFixture.createMockUser();
        Spec mockSpec = SpecFixture.createMockSpec();
        Comment mockComment = CommentFixture.createMockComment();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(specRepository.findByIdAndStatus(DEFAULT_SPEC_ID, SpecStatus.ACTIVE)).willReturn(Optional.of(mockSpec));
            given(commentRepository.findMaxBundleBySpecId(DEFAULT_SPEC_ID)).willReturn(null);
            given(commentRepository.save(Mockito.any(Comment.class))).willReturn(mockComment);

            // when
            CommentPostResponse response = commentService.createComment(DEFAULT_SPEC_ID, commentRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            then(commentRepository).should().save(Mockito.any(Comment.class));
        }
    }
}
