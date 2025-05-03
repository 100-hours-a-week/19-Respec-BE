package kakaotech.bootcamp.respec.specranking.domain.englishskill.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kakaotech.bootcamp.respec.specranking.domain.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LanguageSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Spec spec;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_name", nullable = false, columnDefinition = "VARCHAR(100)")
    private LanguageTest languageTest;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String score;

    public LanguageSkill(Spec spec, LanguageTest languageTest, String score) {
        this.spec = spec;
        this.languageTest = languageTest;
        this.score = score;
    }
}
