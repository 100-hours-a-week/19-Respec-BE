package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

import lombok.Data;

@Data
public class CommentUpdateResponse {
    private Boolean isSuccess;
    private String message;
    private CommentUpdateData data;

    @Data
    public static class CommentUpdateData {
        private Long commentId;
        private String content;
        private String updatedAt;

        public CommentUpdateData(Long commentId, String content, String updatedAt) {
            this.commentId = commentId;
            this.content = content;
            this.updatedAt = updatedAt;
        }
    }

    public CommentUpdateResponse(Boolean isSuccess, String message, CommentUpdateData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }
}
