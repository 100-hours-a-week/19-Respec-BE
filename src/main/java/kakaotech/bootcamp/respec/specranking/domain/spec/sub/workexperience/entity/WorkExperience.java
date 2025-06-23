package kakaotech.bootcamp.respec.specranking.domain.spec.sub.workexperience.entity;

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
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.global.common.type.Position;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private Position position;

    @Column(name = "work_month", nullable = false, columnDefinition = "SMALLINT UNSIGNED")
    private Integer workMonth;

    public WorkExperience(Spec spec, String companyName, Position position, Integer workMonth) {
        this.spec = spec;
        this.companyName = companyName;
        this.position = position;
        this.workMonth = workMonth;
    }
}
