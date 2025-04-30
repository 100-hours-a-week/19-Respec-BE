package kakaotech.bootcamp.respec.specranking.domain.spec.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnglishSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Spec spec;

    @Column(name = "exam_name", nullable = false, columnDefinition = "VARCHAR(100)")
    private String examName;

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String language;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String score;

    public EnglishSkill(Spec spec, String examName, String language, String score) {
        this.spec = spec;
        this.examName = examName;
        this.language = language;
        this.score = score;
    }
}
