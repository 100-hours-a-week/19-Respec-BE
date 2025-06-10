package kakaotech.bootcamp.respec.specranking.domain.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyQueryDto {
    private Long replyId;
    private Long writerId;
    private String content;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer bundle;

    public ReplyQueryDto(Long replyId, Long writerId, String content, String nickname,
                         String profileImageUrl, LocalDateTime createdAt, LocalDateTime updatedAt, Integer bundle) {
        this.replyId = replyId;
        this.writerId = writerId;
        this.content = content;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.bundle = bundle;
    }
}
