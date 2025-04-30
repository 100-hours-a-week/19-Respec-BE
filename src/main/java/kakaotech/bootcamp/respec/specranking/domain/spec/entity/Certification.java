package kakaotech.bootcamp.respec.specranking.domain.spec.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Spec spec;

    @Column(name = "certification_name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String certificationName;

    public Certification(Spec spec, String certificationName) {
        this.spec = spec;
        this.certificationName = certificationName;
    }
}
