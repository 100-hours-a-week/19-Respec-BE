package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class CommentListResponse {
    private Boolean isSuccess;
    private String message;
    private CommentListData data;

    @Data
    public static class CommentListData {
        private List<CommentWithReplies> comments;
        private PageInfo pageInfo;

        public CommentListData(Page<CommentWithReplies> page) {
            this.comments = page.getContent();
            this.pageInfo = new PageInfo(page);
        }
    }

    @Data
    public static class CommentWithReplies {
        private Long commentId;
        private Long writerId;
        private String content;
        private String nickname;
        private String profileImageUrl;
        private String createdAt;
        private String updatedAt;
        private Integer replyCount;
        private List<ReplyInfo> replies;

        public CommentWithReplies(Long commentId, Long writerId, String content, String nickname,
                                  String profileImageUrl, String createdAt, String updatedAt,
                                  Integer replyCount, List<ReplyInfo> replies) {
            this.commentId = commentId;
            this.writerId = writerId;
            this.content = content;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.replyCount = replyCount;
            this.replies = replies;
        }
    }

    @Data
    public static class ReplyInfo {
        private Long replyId;
        private Long writerId;
        private String content;
        private String nickname;
        private String profileImageUrl;
        private String createdAt;
        private String updatedAt;

        public ReplyInfo(Long replyId, Long writerId, String content, String nickname,
                         String profileImageUrl, String createdAt, String updatedAt) {
            this.replyId = replyId;
            this.writerId = writerId;
            this.content = content;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }

    @Data
    public static class PageInfo {
        private Integer pageNumber;
        private Integer pageSize;
        private Long totalElements;
        private Integer totalPages;
        private Boolean isFirst;
        private Boolean isLast;

        public PageInfo(Page<?> page) {
            this.pageNumber = page.getNumber();
            this.pageSize = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.isFirst = page.isFirst();
            this.isLast = page.isLast();
        }
    }

    public CommentListResponse(Boolean isSuccess, String message, CommentListData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }
}
