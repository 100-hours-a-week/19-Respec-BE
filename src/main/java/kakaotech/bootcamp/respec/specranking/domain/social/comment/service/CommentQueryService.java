package kakaotech.bootcamp.respec.specranking.domain.social.comment.service;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;

    public CommentListResponse getComments(Long specId, Pageable pageable) {

        commentValidator.validateSpecExists(specId);

        Page<CommentListResponse.CommentWithReplies> commentsPage = commentRepository.findCommentsWithReplies(specId,
                pageable);
        CommentListResponse.CommentListData commentListData = buildCommentListData(commentsPage);

        return CommentListResponse.success(CommentMessages.GET_COMMENT_LIST_SUCCESS, commentListData);
    }

    private CommentListResponse.CommentListData buildCommentListData(Page<CommentListResponse.CommentWithReplies> commentsPage) {
        return new CommentListResponse.CommentListData(
                commentsPage.getContent(),
                buildPageInfo(commentsPage)
        );
    }

    private CommentListResponse.PageInfo buildPageInfo(Page<CommentListResponse.CommentWithReplies> commentsPage) {
        return new CommentListResponse.PageInfo(
                commentsPage.getNumber(),
                commentsPage.getSize(),
                commentsPage.getTotalElements(),
                commentsPage.getTotalPages(),
                commentsPage.isFirst(),
                commentsPage.isLast()
        );
    }
}
