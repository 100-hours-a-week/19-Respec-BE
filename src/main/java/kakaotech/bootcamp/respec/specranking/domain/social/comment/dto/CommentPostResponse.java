package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

import lombok.Data;

@Data
public class CommentPostResponse {
    private Boolean isSuccess;
    private String message;
    private CommentData data;

    @Data
    public static class CommentData {
        private Long commentId;
        private String nickname;
        private String profileImageUrl;
        private String content;
        private Integer depth;
        private Long parentCommentId;
        private Integer replyCount;

        public CommentData(Long commentId, String nickname, String profileImageUrl,
                           String content, Integer depth, Long parentCommentId, Integer replyCount) {
            this.commentId = commentId;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
            this.content = content;
            this.depth = depth;
            this.parentCommentId = parentCommentId;
            this.replyCount = replyCount;
        }
    }

    public CommentPostResponse(Boolean isSuccess, String message, CommentData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }
}
