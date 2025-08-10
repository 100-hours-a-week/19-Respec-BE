package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

public record ReplyPostResponse (
        boolean isSuccess,
        String message,
        ReplyData data
) {
    public record ReplyData (
            Long commentId,
            String nickname,
            String profileImageUrl,
            String content,
            Integer depth,
            Long parentCommentId
    ) { }

    public static ReplyPostResponse success(String message, ReplyData data) {
        return new ReplyPostResponse(true, message, data);
    }
}
