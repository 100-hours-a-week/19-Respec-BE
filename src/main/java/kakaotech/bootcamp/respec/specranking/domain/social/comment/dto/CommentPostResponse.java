package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

public record CommentPostResponse (
        Boolean isSuccess,
        String message,
        CommentData data
) {
    public record CommentData (
            Long commentId,
            String nickname,
            String profileImageUrl,
            String content,
            Integer depth,
            Long parentCommentId,
            Integer replyCount
    ) { }

    public static CommentPostResponse success(String message, CommentData data) {
        return new CommentPostResponse(true, message, data);
    }
}
