package kakaotech.bootcamp.respec.specranking.fixture;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;

import java.util.List;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;

public class CommentQueryFixture {

    public static CommentListResponse.CommentWithReplies createCommentWithReplies() {
        return createCommentWithReplies(DEFAULT_COMMENT_ID, DEFAULT_COMMENT_CONTENT);
    }

    public static CommentListResponse.CommentWithReplies createCommentWithReplies(Long commentId, String content) {
        return new CommentListResponse.CommentWithReplies(
                commentId,
                DEFAULT_USER_ID,
                content,
                DEFAULT_USER_NICKNAME,
                DEFAULT_USER_PROFILE_URL,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT,
                0,
                List.of()
        );
    }

    public static CommentListResponse.CommentWithReplies createCommentWithRepliesAndReplyList(
            Long commentId, String content, List<CommentListResponse.ReplyInfo> replies, int replyCount) {
        return new CommentListResponse.CommentWithReplies(
                commentId,
                DEFAULT_USER_ID,
                content,
                DEFAULT_USER_NICKNAME,
                DEFAULT_USER_PROFILE_URL,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT,
                replyCount,
                replies
        );
    }

    public static CommentListResponse.ReplyInfo createReplyInfo(Long replyId, String content) {
        return new CommentListResponse.ReplyInfo(
                replyId,
                ANOTHER_USER_ID,
                content,
                ANOTHER_USER_NICKNAME,
                ANOTHER_USER_PROFILE_URL,
                REPLY_CREATED_AT,
                REPLY_UPDATED_AT
        );
    }

    public static CommentListResponse.ReplyInfo createFirstReply() {
        return createReplyInfo(10L, "첫 번째 대댓글");
    }

    public static CommentListResponse.ReplyInfo createSecondReply() {
        return createReplyInfo(11L, "두 번째 대댓글");
    }

    public static CommentListResponse.CommentWithReplies createNumberedComment(Long commentId, int number) {
        return createCommentWithReplies(commentId, number + " 번째 댓글");
    }

    private CommentQueryFixture() { }
}
