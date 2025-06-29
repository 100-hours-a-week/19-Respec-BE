package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

public record CommentUpdateResponse (
        Boolean isSuccess,
        String message,
        CommentUpdateData data
) {
    public record CommentUpdateData (
            Long commentId,
            String content,
            String updatedAt
    ) { }

    public static CommentUpdateResponse success(String message, CommentUpdateData data) {
        return new CommentUpdateResponse(true, message, data);
    }
}
