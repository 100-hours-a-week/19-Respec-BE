package kakaotech.bootcamp.respec.specranking.domain.spec.entity;

import kakaotech.bootcamp.respec.specranking.domain.common.type.EducationInstitute;
import kakaotech.bootcamp.respec.specranking.domain.common.type.EducationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Spec spec;

    @Enumerated(EnumType.STRING)
    @Column(name = "institute", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ENROLLED'")
    private EducationInstitute institute;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'MIDDLE_SCHOOL'")
    private EducationStatus status;

    @Column(name = "analysis_score", nullable = false, columnDefinition = "DOUBLE")
    private Double analysisScore;

    public Education(Spec spec, EducationInstitute institute, EducationStatus status, Double analysisScore) {
        this.spec = spec;
        this.institute = institute;
        this.status = status;
        this.analysisScore = analysisScore;
    }
}
