package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record CommentQueryDto (
        Long commentId,
        Long writerId,
        String content,
        String nickname,
        String profileImageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Integer bundle
) {
    private static final String DELETED_COMMENT_CONTENT = "삭제된 댓글입니다.";
    private static final String DELETED_COMMENT_USER_NICKNAME = "삭제된 사용자";

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public String getMaskedContent() {
        return isDeleted() ? DELETED_COMMENT_CONTENT : content;
    }

    public String getMaskedUserNickname() {
        return isDeleted() ? DELETED_COMMENT_USER_NICKNAME : nickname;
    }

    public String getMaskedProfileImageUrl() {
        return isDeleted() ? null : profileImageUrl;
    }

    public String getFormattedCreatedAt() {
        return formatDateTime(createdAt);
    }

    public String getFormattedUpdatedAt() {
        return formatDateTime(updatedAt);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }
}
