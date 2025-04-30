package kakaotech.bootcamp.respec.specranking.domain.spec.entity;

import kakaotech.bootcamp.respec.specranking.domain.common.BaseTimeEntity;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_work_position_analysis_score", columnList = "work_position, analysis_score"),
        @Index(name = "idx_analysis_score", columnList = "analysis_score")
})
public class Spec extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User user;

    @Column(name = "work_position", nullable = false, columnDefinition = "VARCHAR(50)")
    private String workPosition;

    @Column(name = "education_score", nullable = false, columnDefinition = "DOUBLE")
    private Double educationScore;

    @Column(name = "work_experience_score", nullable = false, columnDefinition = "DOUBLE")
    private Double workExperienceScore;

    @Column(name = "portfolio_score", nullable = false, columnDefinition = "DOUBLE")
    private Double portfolioScore;

    @Column(name = "activity_networking_score", nullable = false, columnDefinition = "DOUBLE")
    private Double activityNetworkingScore;

    @Column(name = "certification_score", nullable = false, columnDefinition = "DOUBLE")
    private Double certificationScore;

    @Column(name = "english_skill_score", nullable = false, columnDefinition = "DOUBLE")
    private Double englishSkillScore;

    @Column(name = "total_analysis_score", nullable = false, columnDefinition = "DOUBLE")
    private Double totalAnalysisScore;

    @Column(name = "bookmark_count", nullable = false, columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long bookmarkCount;

    @Column(name = "comment_count", nullable = false, columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long commentCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
    private SpecStatus status;

    public Spec(User user, String workPosition
            , Double educationScore
            , Double workExperienceScore
            , Double portfolioScore
            , Double activityNetworkingScore
            , Double certificationScore
            , Double englishSkillScore, Double totalAnalysisScore) {
        this.user = user;
        this.workPosition = workPosition;
        this.educationScore = educationScore;
        this.workExperienceScore = workExperienceScore;
        this.portfolioScore = portfolioScore;
        this.activityNetworkingScore = activityNetworkingScore;
        this.certificationScore = certificationScore;
        this.englishSkillScore = englishSkillScore;
        this.totalAnalysisScore = totalAnalysisScore;
        this.bookmarkCount = 0L;
        this.commentCount = 0L;
        this.status = SpecStatus.ACTIVE;
    }
}
