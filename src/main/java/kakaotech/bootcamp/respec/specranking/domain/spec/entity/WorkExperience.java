package kakaotech.bootcamp.respec.specranking.domain.spec.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Spec spec;

    @Column(name = "company_name", nullable = false, columnDefinition = "VARCHAR(50)")
    private String companyName;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'INTERN'")
    private String position;

    @Column(name = "work_month", nullable = false, columnDefinition = "SMALLINT UNSIGNED")
    private Integer workMonth;

    @Column(name = "analysis_score", nullable = false, columnDefinition = "DOUBLE")
    private Double analysisScore;

    public WorkExperience(Spec spec, String companyName, String position, Integer workMonth, Double analysisScore) {
        this.spec = spec;
        this.companyName = companyName;
        this.position = position;
        this.workMonth = workMonth;
        this.analysisScore = analysisScore;
    }
}
