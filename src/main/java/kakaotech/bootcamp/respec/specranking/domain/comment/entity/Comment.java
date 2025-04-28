package kakaotech.bootcamp.respec.specranking.domain.comment.entity;

import kakaotech.bootcamp.respec.specranking.domain.common.BaseTimeEntity;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
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

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String content;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int bundle;

    @Column(nullable = false, columnDefinition = "TINYINT UNSIGNED DEFAULT 0")
    private int depth;

    public Comment(Spec spec, Comment parentComment, String content, int bundle, int depth) {
        this.spec = spec;
        this.parentComment = parentComment;
        this.content = content;
        this.bundle = bundle;
        this.depth = depth;
    }
}
