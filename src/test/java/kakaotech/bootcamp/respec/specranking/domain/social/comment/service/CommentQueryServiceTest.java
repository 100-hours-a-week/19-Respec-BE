package kakaotech.bootcamp.respec.specranking.domain.social.comment.service;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.validator.CommentQueryValidator;
import kakaotech.bootcamp.respec.specranking.fixture.CommentQueryFixture;
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

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 목록 조회 서비스 테스트")
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
        Pageable pageable = PageRequest.of(0, 5);
        CommentListResponse.CommentWithReplies commentWithReplies = CommentQueryFixture.createCommentWithReplies();
        Page<CommentListResponse.CommentWithReplies> commentsPage =
                new PageImpl<>(List.of(commentWithReplies), pageable, 1);

        given(commentRepository.findCommentsWithReplies(DEFAULT_SPEC_ID, pageable)).willReturn(commentsPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(DEFAULT_SPEC_ID, pageable);

        // then
        then(commentQueryValidator).should().validateSpecExists(DEFAULT_SPEC_ID);
        then(commentRepository).should().findCommentsWithReplies(DEFAULT_SPEC_ID, pageable);

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
        Pageable pageable = PageRequest.of(0, 5);
        Page<CommentListResponse.CommentWithReplies> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);

        given(commentRepository.findCommentsWithReplies(DEFAULT_SPEC_ID, pageable)).willReturn(emptyPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(DEFAULT_SPEC_ID, pageable);

        // then
        then(commentQueryValidator).should().validateSpecExists(DEFAULT_SPEC_ID);
        then(commentRepository).should().findCommentsWithReplies(DEFAULT_SPEC_ID, pageable);

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
        Pageable pageable = PageRequest.of(1, 3);
        CommentListResponse.CommentWithReplies comment1 = CommentQueryFixture.createNumberedComment(4L, 4);
        CommentListResponse.CommentWithReplies comment2 = CommentQueryFixture.createNumberedComment(5L, 5);
        CommentListResponse.CommentWithReplies comment3 = CommentQueryFixture.createNumberedComment(6L, 6);

        Page<CommentListResponse.CommentWithReplies> commentsPage =
                new PageImpl<>(List.of(comment1, comment2, comment3), pageable, 8);

        given(commentRepository.findCommentsWithReplies(DEFAULT_SPEC_ID, pageable)).willReturn(commentsPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(DEFAULT_SPEC_ID, pageable);

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
        Pageable firstPage = PageRequest.of(0, 5);
        CommentListResponse.CommentWithReplies comment = CommentQueryFixture.createCommentWithReplies();
        Page<CommentListResponse.CommentWithReplies> commentsPage =
                new PageImpl<>(List.of(comment), firstPage, 10);

        given(commentRepository.findCommentsWithReplies(DEFAULT_SPEC_ID, firstPage)).willReturn(commentsPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(DEFAULT_SPEC_ID, firstPage);

        // then
        CommentListResponse.PageInfo pageInfo = commentListResponse.data().pageInfo();
        assertThat(pageInfo.isFirst()).isTrue();
        assertThat(pageInfo.isLast()).isFalse();
    }

    @Test
    @DisplayName("성공: 마지막 페이지인 경우")
    void getComments_LastPage_ReturnsCorrectPageInfo() {
        // given
        Pageable lastPage = PageRequest.of(1, 5);
        CommentListResponse.CommentWithReplies comment = CommentQueryFixture.createCommentWithReplies();
        Page<CommentListResponse.CommentWithReplies> commentsPage =
                new PageImpl<>(List.of(comment), lastPage, 6);

        given(commentRepository.findCommentsWithReplies(DEFAULT_SPEC_ID, lastPage)).willReturn(commentsPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(DEFAULT_SPEC_ID, lastPage);

        // then
        CommentListResponse.PageInfo pageInfo = commentListResponse.data().pageInfo();
        assertThat(pageInfo.isFirst()).isFalse();
        assertThat(pageInfo.isLast()).isTrue();
    }

    @Test
    @DisplayName("성공: 대댓글이 포함된 댓글")
    void getComments_WithReplies_ReturnsCommentsWithReplies() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        CommentListResponse.ReplyInfo reply1 = CommentQueryFixture.createFirstReply();
        CommentListResponse.ReplyInfo reply2 = CommentQueryFixture.createSecondReply();

        CommentListResponse.CommentWithReplies commentWithReplies =
                CommentQueryFixture.createCommentWithRepliesAndReplyList(
                        DEFAULT_COMMENT_ID, DEFAULT_COMMENT_CONTENT, List.of(reply1, reply2), 2);

        Page<CommentListResponse.CommentWithReplies> commentsPage =
                new PageImpl<>(List.of(commentWithReplies), pageable, 1);

        given(commentRepository.findCommentsWithReplies(DEFAULT_SPEC_ID, pageable)).willReturn(commentsPage);

        // when
        CommentListResponse commentListResponse = commentQueryService.getComments(DEFAULT_SPEC_ID, pageable);

        // then
        CommentListResponse.CommentWithReplies result = commentListResponse.data().comments().getFirst();
        assertThat(result.replyCount()).isEqualTo(2);
        assertThat(result.replies()).hasSize(2);
        assertThat(result.replies()).containsExactly(reply1, reply2);
    }
}