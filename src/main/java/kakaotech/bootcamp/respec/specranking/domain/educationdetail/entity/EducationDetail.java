package kakaotech.bootcamp.respec.specranking.domain.educationdetail.entity;

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
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.education.entity.Education;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EducationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Education education;

    @Column(name = "school_name", nullable = false, columnDefinition = "VARCHAR(50)")
    private String schoolName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'COMPLETION'")
    private Degree degree;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String major;

    @Column(nullable = false, columnDefinition = "DOUBLE")
    private Double gpa;

    @Column(name = "max_gpa", nullable = false, columnDefinition = "DOUBLE DEFAULT 4.0")
    private Double maxGpa;

    public EducationDetail(Education education, String schoolName, Degree degree,
                           String major, Double gpa, Double maxGpa) {
        this.education = education;
        this.schoolName = schoolName;
        this.degree = degree;
        this.major = major;
        this.gpa = gpa;
        this.maxGpa = maxGpa;
    }
}
