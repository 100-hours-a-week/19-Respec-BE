package kakaotech.bootcamp.respec.specranking.domain.social.comment.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentRequest;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentUpdateResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.ReplyPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private static final int ROOT_COMMENT_DEPTH = 0;
    private static final int REPLY_DEPTH = 1;
    private static final int INITIAL_BUNDLE_NUMBER = 1;
    private static final int INITIAL_REPLY_COUNT = 0;

    private final CommentRepository commentRepository;
    private final SpecRepository specRepository;
    private final UserRepository userRepository;

    public CommentPostResponse createComment(Long specId, CommentRequest request) {
        Long userId = getCurrentUserIdOrThrow();
        User user = findUserById(userId);
        Spec targetSpec = findActiveSpecById(specId);

        int newBundleNumber = generateNewBundleNumber(specId);

        Comment comment = new Comment(targetSpec, user, null, request.getContent(), newBundleNumber, ROOT_COMMENT_DEPTH);
        Comment savedComment = commentRepository.save(comment);

        return buildCommentPostResponse(savedComment, user);
    }

    public ReplyPostResponse createReply(Long specId, Long commentId, CommentRequest request) {
        Long userId = getCurrentUserIdOrThrow();
        User user = findUserById(userId);
        Spec targetSpec = findActiveSpecById(specId);
        Comment parentComment = findParentComment(commentId);

        validateCommentBelongsToSpec(parentComment, specId);
        validateReplyDepth(parentComment);

        Comment reply = new Comment(targetSpec, user, parentComment, request.getContent(), parentComment.getBundle(), REPLY_DEPTH);
        Comment savedReply = commentRepository.save(reply);

        return buildReplyPostResponse(savedReply, user, parentComment);
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

        if (!comment.getWriter().getId().equals(userId)) {
            throw new IllegalArgumentException("댓글 삭제는 작성자 본인만 가능합니다.");
        }
        if (comment.getDeletedAt() != null) {
            throw new IllegalStateException("이미 삭제된 댓글입니다.");
        }

        comment.delete();
        commentRepository.save(comment);

        return new SimpleResponseDto(true, "댓글 삭제 성공");
    }

    private Long getCurrentUserIdOrThrow() {
        return UserUtils.getCurrentUserId()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Spec findActiveSpecById(Long specId) {
        return specRepository.findByIdAndStatus(specId, SpecStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SPEC_NOT_FOUND));
    }

    private Comment findParentComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private int generateNewBundleNumber(Long specId) {
        Integer maxBundle = commentRepository.findMaxBundleBySpecId(specId);
        return (maxBundle == null) ? INITIAL_BUNDLE_NUMBER : maxBundle + 1;
    }

    private void validateCommentBelongsToSpec(Comment comment, Long specId) {
        if (!comment.getSpec().getId().equals(specId)) {
            throw new CustomException(ErrorCode.COMMENT_SPEC_MISMATCH);
        }
    }

    private void validateReplyDepth(Comment parentComment) {
        if (parentComment.getDepth() != ROOT_COMMENT_DEPTH) {
            throw new CustomException(ErrorCode.REPLY_DEPTH_EXCEEDED);
        }
    }

    private CommentPostResponse buildCommentPostResponse(Comment comment, User user) {
        CommentPostResponse.CommentData commentData = new CommentPostResponse.CommentData(
                comment.getId(),
                user.getNickname(),
                user.getUserProfileUrl(),
                comment.getContent(),
                comment.getDepth(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                INITIAL_REPLY_COUNT
        );

        return CommentPostResponse.success(CommentMessages.COMMENT_CREATE_SUCCESS, commentData);
    }

    private ReplyPostResponse buildReplyPostResponse(Comment reply, User user, Comment parentComment) {
        ReplyPostResponse.ReplyData replyData = new ReplyPostResponse.ReplyData(
                reply.getId(),
                user.getNickname(),
                user.getUserProfileUrl(),
                reply.getContent(),
                reply.getDepth(),
                parentComment.getId()
        );

        return ReplyPostResponse.success(CommentMessages.REPLY_CREATE_SUCCESS, replyData);
    }
}
