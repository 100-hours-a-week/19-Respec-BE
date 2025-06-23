package kakaotech.bootcamp.respec.specranking.domain.spec.sub.activitynetworking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.entity.Spec;
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

    public ActivityNetworking(Spec spec, String activityName, String position, String award) {
        this.spec = spec;
        this.activityName = activityName;
        this.position = position;
        this.award = award;
    }
}
