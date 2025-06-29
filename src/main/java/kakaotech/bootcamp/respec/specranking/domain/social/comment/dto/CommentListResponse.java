package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

import java.util.List;

public record CommentListResponse (
        Boolean isSuccess,
        String message,
        CommentListData data
) {
    public record CommentListData (
            List<CommentWithReplies> comments,
            PageInfo pageInfo
    ) { }

    public record CommentWithReplies (
            Long commentId,
            Long writerId,
            String content,
            String nickname,
            String profileImageUrl,
            String createdAt,
            String updatedAt,
            Integer replyCount,
            List<ReplyInfo> replies
    ) { }

    public record ReplyInfo (
            Long replyId,
            Long writerId,
            String content,
            String nickname,
            String profileImageUrl,
            String createdAt,
            String updatedAt
    ) { }

    public record PageInfo (
            Integer pageNumber,
            Integer pageSize,
            Long totalElements,
            Integer totalPages,
            Boolean isFirst,
            Boolean isLast
    ) { }

    public static CommentListResponse success(String message, CommentListData data) {
        return new CommentListResponse(true, message, data);
    }
}
