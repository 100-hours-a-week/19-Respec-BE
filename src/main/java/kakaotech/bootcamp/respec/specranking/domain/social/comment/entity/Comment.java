package kakaotech.bootcamp.respec.specranking.domain.social.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.global.common.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_spec_id", columnList = "spec_id")
        })
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Spec spec;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", columnDefinition = "BIGINT UNSIGNED")
    private Comment parentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", columnDefinition = "BIGINT UNSIGNED")
    private User writer;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String content;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private Integer bundle;

    @Column(nullable = false, columnDefinition = "TINYINT UNSIGNED DEFAULT 0")
    private Integer depth;

    public Comment(Spec spec, User writer, Comment parentComment, String content, int bundle, int depth) {
        this.spec = spec;
        this.writer = writer;
        this.parentComment = parentComment;
        this.content = content;
        this.bundle = bundle;
        this.depth = depth;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }
}
