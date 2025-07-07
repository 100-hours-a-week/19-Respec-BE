package kakaotech.bootcamp.respec.specranking.fixture;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import org.mockito.Mockito;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;
import static org.mockito.Mockito.lenient;

public class CommentFixture {

    public static Comment createMockComment() {
        return createMockComment(DEFAULT_COMMENT_ID, DEFAULT_COMMENT_CONTENT, ROOT_COMMENT_DEPTH, null);
    }

    public static Comment createMockParentComment() {
        return createMockComment(DEFAULT_PARENT_COMMENT_ID, DEFAULT_COMMENT_CONTENT, ROOT_COMMENT_DEPTH, null);
    }

    public static Comment createMockReply() {
        return createMockComment(REPLY_ID, REPLY_CONTENT, REPLY_DEPTH, DEFAULT_PARENT_COMMENT_ID);
    }

    public static Comment createMockComment(Long commentId, String content, Integer depth, Long parentCommentId) {
        Comment comment = Mockito.mock(Comment.class);
        lenient().when(comment.getId()).thenReturn(commentId);
        lenient().when(comment.getContent()).thenReturn(content);
        lenient().when(comment.getDepth()).thenReturn(depth);
        lenient().when(comment.getParentCommentId()).thenReturn(parentCommentId);
        return comment;
    }

    public static Comment createMockNonRootComment() {
        Comment comment = createMockReply();
        lenient().when(comment.isRootComment()).thenReturn(false);
        return comment;
    }

    private CommentFixture() { }
}
