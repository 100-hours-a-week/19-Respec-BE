package kakaotech.bootcamp.respec.specranking.domain.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentQueryDto {
    private Long commentId;
    private Long writerId;
    private String content;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer bundle;

    public CommentQueryDto(Long commentId, Long writerId, String content, String nickname,
                           String profileImageUrl, LocalDateTime createdAt, LocalDateTime updatedAt,
                           LocalDateTime deletedAt, Integer bundle) {
        this.commentId = commentId;
        this.writerId = writerId;
        this.content = content;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.bundle = bundle;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
