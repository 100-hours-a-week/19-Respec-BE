package kakaotech.bootcamp.respec.specranking.domain.social.comment.service;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.validator.CommentValidator;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.fixture.CommentFixture;
import kakaotech.bootcamp.respec.specranking.fixture.UserFixture;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 삭제 서비스 테스트")
public class CommentDeleteServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("성공: 정상적인 댓글 삭제")
    void deleteComment_Success() {
        // given
        User mockUser = UserFixture.createMockUser();
        Comment mockComment = CommentFixture.createMockComment();

        try (MockedStatic<UserUtils> mockUserUtils = Mockito.mockStatic(UserUtils.class)) {
            UserFixture.setupAuthenticatedUser(mockUserUtils);

            given(userRepository.findById(DEFAULT_USER_ID)).willReturn(Optional.of(mockUser));
            given(commentRepository.findByIdAndSpecId(DEFAULT_COMMENT_ID, DEFAULT_SPEC_ID)).willReturn(Optional.of(mockComment));
            willDoNothing().given(commentValidator).validateCommentDeletion(mockComment, mockUser);
            given(commentRepository.save(mockComment)).willReturn(mockComment);

            // when
            SimpleResponseDto response = commentService.deleteComment(DEFAULT_SPEC_ID, DEFAULT_COMMENT_ID);

            // then
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.message()).isEqualTo(CommentMessages.COMMENT_DELETE_SUCCESS);

            then(commentValidator).should().validateCommentDeletion(mockComment, mockUser);
            then(commentRepository).should().save(mockComment);
        }
    }
}
