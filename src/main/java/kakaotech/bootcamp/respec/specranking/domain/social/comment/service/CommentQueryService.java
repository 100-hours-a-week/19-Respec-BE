package kakaotech.bootcamp.respec.specranking.domain.social.comment.service;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
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
    private final SpecRepository specRepository;

    public CommentListResponse getComments(Long specId, Pageable pageable) {
        specRepository.findByIdAndStatus(specId, SpecStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 비활성화된 스펙입니다. ID: " + specId));

        Page<CommentListResponse.CommentWithReplies> commentsPage = commentRepository.findCommentsWithReplies(specId,
                pageable);

        CommentListResponse.CommentListData data = new CommentListResponse.CommentListData(commentsPage);

        return new CommentListResponse(true, "댓글 및 대댓글 목록 조회 성공", data);
    }
}
