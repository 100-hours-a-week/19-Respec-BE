package kakaotech.bootcamp.respec.specranking.domain.comment.service;

import kakaotech.bootcamp.respec.specranking.domain.comment.dto.CommentRequest;
import kakaotech.bootcamp.respec.specranking.domain.comment.dto.CommentPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.comment.dto.CommentUpdateResponse;
import kakaotech.bootcamp.respec.specranking.domain.comment.dto.ReplyPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final SpecRepository specRepository;
    private final UserRepository userRepository;

    public CommentPostResponse createComment(Long specId, CommentRequest request) {
        Optional<Long> optUserId = UserUtils.getCurrentUserId();
        Long userId = optUserId.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Spec spec = specRepository.findByIdAndStatus(specId, SpecStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("스펙을 찾을 수 없습니다. ID: " + specId));

        Integer maxBundle = commentRepository.findMaxBundleBySpecId(specId);
        int newBundle = (maxBundle == null) ? 1 : maxBundle + 1;

        Comment comment = new Comment(spec, user, null, request.getContent(), newBundle, 0);
        Comment savedComment = commentRepository.save(comment);

        CommentPostResponse.CommentData commentData = new CommentPostResponse.CommentData(
                savedComment.getId(),
                user.getNickname(),
                user.getUserProfileUrl(),
                savedComment.getContent(),
                savedComment.getDepth(),
                savedComment.getParentComment() != null ? savedComment.getParentComment().getId() : null,
                0
        );

        return new CommentPostResponse(true, "댓글 작성 성공", commentData);
    }

    public ReplyPostResponse createReply(Long specId, Long commentId, CommentRequest request) {
        Optional<Long> optUserId = UserUtils.getCurrentUserId();
        Long userId = optUserId.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Spec spec = specRepository.findByIdAndStatus(specId, SpecStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("스펙을 찾을 수 없습니다. ID: " + specId));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다. ID: " + commentId));

        if (!parentComment.getSpec().getId().equals(specId)) {
            throw new IllegalArgumentException("부모 댓글이 해당 스펙에 속하지 않습니다.");
        }

        if (parentComment.getDepth() != 0) {
            throw new IllegalArgumentException("대댓글에는 답글을 작성할 수 없습니다. 최상위 댓글에만 답글을 작성해주세요.");
        }

        Comment reply = new Comment(spec, user, parentComment, request.getContent(), parentComment.getBundle(), 1);
        Comment savedReply = commentRepository.save(reply);

        ReplyPostResponse.ReplyData replyData = new ReplyPostResponse.ReplyData(
                savedReply.getId(),
                user.getNickname(),
                user.getUserProfileUrl(),
                savedReply.getContent(),
                savedReply.getDepth(),
                parentComment.getId()
        );

        return new ReplyPostResponse(true, "대댓글 작성 성공", replyData);
    }

    public CommentUpdateResponse updateComment(Long specId, Long commentId, CommentRequest request) {
        Optional<Long> optUserId = UserUtils.getCurrentUserId();
        Long userId = optUserId.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        Comment comment = commentRepository.findByIdAndSpecId(commentId, specId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글이거나 해당 스펙에 속하지 않는 댓글입니다. ID: " + commentId));

        User writer = comment.getWriter();
        if (!writer.getId().equals(userId)) {
            throw new IllegalArgumentException("댓글 수정은 작성자 본인만 가능합니다. ID: " + writer.getId());
        }

        comment.updateContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);

        String updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        CommentUpdateResponse.CommentUpdateData updateData = new CommentUpdateResponse.CommentUpdateData(
                updatedComment.getId(),
                updatedComment.getContent(),
                updatedAt
        );

        return new CommentUpdateResponse(true, "댓글 수정 성공", updateData);
    }

    public SimpleResponseDto deleteComment(Long specId, Long commentId) {
        Optional<Long> optUserId = UserUtils.getCurrentUserId();
        Long userId = optUserId.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        Comment comment = commentRepository.findByIdAndSpecId(commentId, specId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글이거나 해당 스펙에 속하지 않는 댓글입니다. ID: " + commentId));

        if (!comment.getWriter().getId().equals(userId)) { throw new IllegalArgumentException("댓글 삭제는 작성자 본인만 가능합니다."); }
        if (comment.getDeletedAt() != null) { throw new IllegalStateException("이미 삭제된 댓글입니다."); }

        comment.delete();
        commentRepository.save(comment);

        return new SimpleResponseDto(true, "댓글 삭제 성공");
    }
}
