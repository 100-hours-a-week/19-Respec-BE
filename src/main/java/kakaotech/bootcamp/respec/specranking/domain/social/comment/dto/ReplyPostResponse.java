package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

import lombok.Data;

@Data
public class ReplyPostResponse {
    private Boolean isSuccess;
    private String message;
    private ReplyData data;

    @Data
    public static class ReplyData {
        private Long commentId;
        private String nickname;
        private String profileImageUrl;
        private String content;
        private Integer depth;
        private Long parentCommentId;

        public ReplyData(Long commentId, String nickname, String profileImageUrl,
                           String content, Integer depth, Long parentCommentId) {
            this.commentId = commentId;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
            this.content = content;
            this.depth = depth;
            this.parentCommentId = parentCommentId;
        }
    }

    public ReplyPostResponse(Boolean isSuccess, String message, ReplyData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }
}
