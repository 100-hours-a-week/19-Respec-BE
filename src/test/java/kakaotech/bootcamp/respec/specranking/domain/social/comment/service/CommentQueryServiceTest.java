package kakaotech.bootcamp.respec.specranking.domain.social.comment.service;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.validator.CommentQueryValidator;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentQueryService 테스트")
class CommentQueryServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentQueryValidator commentQueryValidator;

    @InjectMocks
    private CommentQueryService commentQueryService;

    @Test
    @DisplayName("성공: 댓글과 대댓글이 있는 경우")
    void getComments_WithCommentsAndReplies_ReturnsCommentListResponse() {
        // given
        Long specId = 1L;
        Pageable pageable = PageRequest.of(0, 5);

        CommentListResponse.CommentWithReplies commentWithReplies = createCommentWithReplies();
        Page<CommentListResponse.CommentWithReplies> commentsPage =
                new PageImpl<>(List.of(commentWithReplies), pageable, 1);

        given(commentRepository.findCommentsWithReplies(specId, pageable))
                .willReturn(commentsPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(specId, pageable);

        // then
        then(commentQueryValidator).should().validateSpecExists(specId);
        then(commentRepository).should().findCommentsWithReplies(specId, pageable);

        assertThat(commentListResponse).isNotNull();
        assertThat(commentListResponse.isSuccess()).isTrue();
        assertThat(commentListResponse.message()).isEqualTo(CommentMessages.GET_COMMENT_LIST_SUCCESS);
        assertThat(commentListResponse.data()).isNotNull();
        assertThat(commentListResponse.data().comments()).hasSize(1);
        assertThat(commentListResponse.data().pageInfo()).isNotNull();
    }

    @Test
    @DisplayName("성공: 댓글이 없는 경우")
    void getComments_WithNoComments_ReturnsEmptyListResponse() {
        // given
        Long specId = 1L;
        Pageable pageable = PageRequest.of(0, 5);

        Page<CommentListResponse.CommentWithReplies> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);

        given(commentRepository.findCommentsWithReplies(specId, pageable))
                .willReturn(emptyPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(specId, pageable);

        // then
        then(commentQueryValidator).should().validateSpecExists(specId);
        then(commentRepository).should().findCommentsWithReplies(specId, pageable);

        assertThat(commentListResponse).isNotNull();
        assertThat(commentListResponse.isSuccess()).isTrue();
        assertThat(commentListResponse.message()).isEqualTo(CommentMessages.GET_COMMENT_LIST_SUCCESS);
        assertThat(commentListResponse.data().comments()).isEmpty();
        assertThat(commentListResponse.data().pageInfo().totalElements()).isZero();
    }

    @Test
    @DisplayName("성공: 페이징 정보가 올바르게 설정됨")
    void getComments_WithPaging_ReturnsCorrectPageInfo() {
        // given
        Long specId = 1L;
        Pageable pageable = PageRequest.of(1, 3);
        CommentListResponse.CommentWithReplies comment1 = createCommentWithReplies(1L, "첫 번째 댓글");
        CommentListResponse.CommentWithReplies comment2 = createCommentWithReplies(2L, "두 번째 댓글");
        CommentListResponse.CommentWithReplies comment3 = createCommentWithReplies(3L, "세 번째 댓글");

        Page<CommentListResponse.CommentWithReplies> commentsPage =
                new PageImpl<>(List.of(comment1, comment2, comment3), pageable, 8);

        given(commentRepository.findCommentsWithReplies(specId, pageable))
                .willReturn(commentsPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(specId, pageable);

        // then
        CommentListResponse.PageInfo pageInfo = commentListResponse.data().pageInfo();
        assertThat(pageInfo.pageNumber()).isEqualTo(1);
        assertThat(pageInfo.pageSize()).isEqualTo(3);
        assertThat(pageInfo.totalElements()).isEqualTo(8);
        assertThat(pageInfo.totalPages()).isEqualTo(3);
        assertThat(pageInfo.isFirst()).isFalse();
        assertThat(pageInfo.isLast()).isFalse();
    }

    @Test
    @DisplayName("성공: 첫 번째 페이지인 경우")
    void getComments_FirstPage_ReturnsCorrectPageInfo() {
        // given
        Long specId = 1L;
        Pageable firstPage = PageRequest.of(0, 5);
        Page<CommentListResponse.CommentWithReplies> commentsPage =
                new PageImpl<>(List.of(createCommentWithReplies()), firstPage, 10);

        given(commentRepository.findCommentsWithReplies(specId, firstPage))
                .willReturn(commentsPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(specId, firstPage);

        // then
        CommentListResponse.PageInfo pageInfo = commentListResponse.data().pageInfo();
        assertThat(pageInfo.isFirst()).isTrue();
        assertThat(pageInfo.isLast()).isFalse();
    }

    @Test
    @DisplayName("성공: 마지막 페이지인 경우")
    void getComments_LastPage_ReturnsCorrectPageInfo() {
        // given
        Long specId = 1L;
        Pageable lastPage = PageRequest.of(1, 5);
        Page<CommentListResponse.CommentWithReplies> commentsPage =
                new PageImpl<>(List.of(createCommentWithReplies()), lastPage, 6);

        given(commentRepository.findCommentsWithReplies(specId, lastPage))
                .willReturn(commentsPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(specId, lastPage);

        // then
        CommentListResponse.PageInfo pageInfo = commentListResponse.data().pageInfo();
        assertThat(pageInfo.isFirst()).isFalse();
        assertThat(pageInfo.isLast()).isTrue();
    }

    @Test
    @DisplayName("성공: 대댓글이 포함된 댓글")
    void getComments_WithReplies_ReturnsCommentsWithReplies() {
        // given
        Long specId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        CommentListResponse.ReplyInfo reply1 = createReplyInfo(10L, "첫 번째 대댓글");
        CommentListResponse.ReplyInfo reply2 = createReplyInfo(11L, "두 번째 대댓글");

        CommentListResponse.CommentWithReplies commentWithReplies =
                createCommentWithRepliesAndReplyList(1L, "댓글 내용", List.of(reply1, reply2), 2);

        Page<CommentListResponse.CommentWithReplies> commentsPage =
                new PageImpl<>(List.of(commentWithReplies), pageable, 1);

        given(commentRepository.findCommentsWithReplies(specId, pageable))
                .willReturn(commentsPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(specId, pageable);

        // then
        CommentListResponse.CommentWithReplies result = commentListResponse.data().comments().getFirst();
        assertThat(result.replyCount()).isEqualTo(2);
        assertThat(result.replies()).hasSize(2);
        assertThat(result.replies().get(0).content()).isEqualTo("첫 번째 대댓글");
        assertThat(result.replies().get(1).content()).isEqualTo("두 번째 대댓글");
    }

    @Test
    @DisplayName("실패: 존재하지 않는 스펙 ID")
    void getComments_WithNotExistingSpecId_ThrowsException() {
        // given
        Long specId = 1L;
        Pageable pageable = PageRequest.of(0, 5);

        willThrow(new CustomException(ErrorCode.SPEC_NOT_FOUND))
                .given(commentQueryValidator).validateSpecExists(specId);

        // when & then
        assertThatThrownBy(() -> commentQueryService.getComments(specId, pageable))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.SPEC_NOT_FOUND.getMessage());

        then(commentQueryValidator).should().validateSpecExists(specId);
        then(commentRepository).shouldHaveNoInteractions();
    }

    private CommentListResponse.CommentWithReplies createCommentWithReplies() {
        return createCommentWithReplies(1L, "테스트 댓글 내용");
    }

    private CommentListResponse.CommentWithReplies createCommentWithReplies(Long commentId, String content) {
        return new CommentListResponse.CommentWithReplies(
                commentId,
                1L,
                content,
                "테스트유저",
                "https://example.com/profile.jpg",
                "2025-01-01T10:00:00",
                "2025-01-01T10:00:00",
                0,
                List.of()
        );
    }

    private CommentListResponse.CommentWithReplies createCommentWithRepliesAndReplyList(
            Long commentId, String content, List<CommentListResponse.ReplyInfo> replies, int replyCount) {
        return new CommentListResponse.CommentWithReplies(
                commentId,
                1L,
                content,
                "테스트유저",
                "https://example.com/profile.jpg",
                "2025-01-01T10:00:00",
                "2025-01-01T10:00:00",
                replyCount,
                replies
        );
    }

    private CommentListResponse.ReplyInfo createReplyInfo(Long replyId, String content) {
        return new CommentListResponse.ReplyInfo(
                replyId,
                2L,
                content,
                "대댓글유저",
                "https://example.com/reply-profile.jpg",
                "2025-01-01T11:00:00",
                "2025-01-01T11:00:00"
        );
    }
}