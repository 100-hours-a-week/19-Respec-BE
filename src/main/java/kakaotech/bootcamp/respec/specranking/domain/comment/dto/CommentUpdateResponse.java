package kakaotech.bootcamp.respec.specranking.domain.comment.dto;

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

        public CommentUpdateData(Long commentId, String content) {
            this.commentId = commentId;
            this.content = content;
        }
    }

    public CommentUpdateResponse(Boolean isSuccess, String message, CommentUpdateData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }
}
