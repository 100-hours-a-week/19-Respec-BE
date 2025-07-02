package kakaotech.bootcamp.respec.specranking.domain.social.comment.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentRequest;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentUpdateResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.ReplyPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.validator.CommentValidator;
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

    private static final int INITIAL_BUNDLE_NUMBER = 1;
    private static final int INITIAL_REPLY_COUNT = 0;

    private final CommentRepository commentRepository;
    private final SpecRepository specRepository;
    private final UserRepository userRepository;
    private final CommentValidator commentValidator;

    public CommentPostResponse createComment(Long specId, CommentRequest request) {
        User user = getCurrentUser();
        Spec targetSpec = findActiveSpecById(specId);

        int newBundleNumber = generateNewBundleNumber(specId);

        Comment comment = Comment.createRootComment(targetSpec, user, request.content(), newBundleNumber);
        Comment savedComment = commentRepository.save(comment);

        return buildCommentPostResponse(savedComment, user);
    }

    public ReplyPostResponse createReply(Long specId, Long commentId, CommentRequest request) {
        User user = getCurrentUser();
        Spec targetSpec = findActiveSpecById(specId);
        Comment parentComment = findCommentByIdAndSpecId(commentId, specId);

        commentValidator.validateReplyCreation(parentComment, targetSpec);

        Comment reply = Comment.createReply(targetSpec, user, parentComment, request.content());
        Comment savedReply = commentRepository.save(reply);

        return buildReplyPostResponse(savedReply, user, parentComment);
    }

    public CommentUpdateResponse updateComment(Long specId, Long commentId, CommentRequest request) {
        User user = getCurrentUser();
        Comment targetComment = findCommentByIdAndSpecId(commentId, specId);

        commentValidator.validateCommentUpdate(targetComment, user);

        targetComment.updateContent(request.content());
        Comment updatedComment = commentRepository.save(targetComment);

        return buildCommentUpdateResponse(updatedComment);
    }

    public SimpleResponseDto deleteComment(Long specId, Long commentId) {
        User currentUser = getCurrentUser();
        Comment targetComment = findCommentByIdAndSpecId(commentId, specId);

        commentValidator.validateCommentDeletion(targetComment, currentUser);

        targetComment.delete();
        commentRepository.save(targetComment);

        return SimpleResponseDto.success(CommentMessages.COMMENT_DELETE_SUCCESS);
    }

    private User getCurrentUser() {
        Long userId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private Spec findActiveSpecById(Long specId) {
        return specRepository.findByIdAndStatus(specId, SpecStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SPEC_NOT_FOUND));
    }

    private Comment findCommentByIdAndSpecId(Long commentId, Long specId) {
        return commentRepository.findByIdAndSpecId(commentId, specId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private int generateNewBundleNumber(Long specId) {
        Integer maxBundle = commentRepository.findMaxBundleBySpecId(specId);
        return (maxBundle == null) ? INITIAL_BUNDLE_NUMBER : maxBundle + 1;
    }

    private CommentPostResponse buildCommentPostResponse(Comment comment, User user) {
        CommentPostResponse.CommentData commentData = new CommentPostResponse.CommentData(
                comment.getId(),
                user.getNickname(),
                user.getUserProfileUrl(),
                comment.getContent(),
                comment.getDepth(),
                comment.getParentCommentId(),
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

    private CommentUpdateResponse buildCommentUpdateResponse(Comment comment) {
        String updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        CommentUpdateResponse.CommentUpdateData commentUpdateData = new CommentUpdateResponse.CommentUpdateData(
                comment.getId(),
                comment.getContent(),
                updatedAt
        );

        return CommentUpdateResponse.success(CommentMessages.COMMENT_UPDATE_SUCCESS, commentUpdateData);
    }
}
