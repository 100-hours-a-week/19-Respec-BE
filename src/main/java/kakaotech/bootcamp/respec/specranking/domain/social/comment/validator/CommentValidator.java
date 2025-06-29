package kakaotech.bootcamp.respec.specranking.domain.social.comment.validator;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final SpecRepository specRepository;

    public void validateReplyCreation(Comment parentComment, Spec spec) {
        validateCommentBelongsToSpec(parentComment, spec);
        validateReplyDepth(parentComment);
    }

    public void validateCommentDeletion(Comment comment, User user) {
        validateCommentOwner(comment, user);
        validateCommentNotDeleted(comment);
    }

    public void validateCommentOwner(Comment comment, User user) {
        if (!comment.isWrittenBy(user)) {
            throw new CustomException(ErrorCode.COMMENT_ACCESS_DENIED);
        }
    }

    public void validateSpecExists(Long specId) {
        specRepository.findByIdAndStatus(specId, SpecStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SPEC_NOT_FOUND));
    }

    private void validateCommentBelongsToSpec(Comment comment, Spec spec) {
        if (!comment.belongsToSpec(spec)) {
            throw new CustomException(ErrorCode.COMMENT_SPEC_MISMATCH);
        }
    }

    private void validateReplyDepth(Comment parentComment) {
        if (!parentComment.isRootComment()) {
            throw new CustomException(ErrorCode.REPLY_DEPTH_EXCEEDED);
        }
    }

    private void validateCommentNotDeleted(Comment comment) {
        if (comment.isDeleted()) {
            throw new CustomException(ErrorCode.COMMENT_ALREADY_DELETED);
        }
    }
}
