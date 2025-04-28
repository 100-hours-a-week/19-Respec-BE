package kakaotech.bootcamp.respec.specranking.domain.spec.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityNetworking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Spec spec;

    @Column(name = "activity_name", nullable = false, columnDefinition = "VARCHAR(50)")
    private String activityName;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String position;

    @Column(columnDefinition = "VARCHAR(50)")
    private String award;

    @Column(name = "analysis_score", nullable = false, columnDefinition = "DOUBLE")
    private Double analysisScore;

    public ActivityNetworking(Spec spec, String activityName, String position, String award, Double analysisScore) {
        this.spec = spec;
        this.activityName = activityName;
        this.position = position;
        this.award = award;
        this.analysisScore = analysisScore;
    }
}
