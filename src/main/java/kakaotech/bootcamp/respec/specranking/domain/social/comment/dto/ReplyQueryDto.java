package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ReplyQueryDto (
        Long replyId,
        Long writerId,
        String content,
        String nickname,
        String profileImageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer bundle
) {
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
