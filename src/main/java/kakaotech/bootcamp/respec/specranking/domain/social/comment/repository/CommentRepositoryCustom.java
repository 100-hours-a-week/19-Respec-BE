package kakaotech.bootcamp.respec.specranking.domain.social.comment.repository;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<CommentListResponse.CommentWithReplies> findCommentsWithReplies(Long specId, Pageable pageable);
}
