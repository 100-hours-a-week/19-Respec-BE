package kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.global.common.entity.BaseTimeEntity;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.response.AiPostSpecResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_job_field_total_analysis_score", columnList = "job_field, total_analysis_score"),
        @Index(name = "idx_total_analysis_score", columnList = "total_analysis_score")
})
public class Spec extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_field", nullable = false, columnDefinition = "VARCHAR(100)")
    private JobField jobField;

    @Column(name = "education_score", nullable = false, columnDefinition = "DOUBLE")
    private Double educationScore;

    @Column(name = "work_experience_score", nullable = false, columnDefinition = "DOUBLE")
    private Double workExperienceScore;

    @Column(name = "activity_networking_score", nullable = false, columnDefinition = "DOUBLE")
    private Double activityNetworkingScore;

    @Column(name = "certification_score", nullable = false, columnDefinition = "DOUBLE")
    private Double certificationScore;

    @Column(name = "english_skill_score", nullable = false, columnDefinition = "DOUBLE")
    private Double englishSkillScore;

    @Column(name = "total_analysis_score", nullable = false, columnDefinition = "DOUBLE")
    private Double totalAnalysisScore;

    @Column(name = "assessment", nullable = false, columnDefinition = "VARCHAR(255)")
    private String assessment;

    @Column(name = "bookmark_count", nullable = false, columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long bookmarkCount;

    @Column(name = "comment_count", nullable = false, columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long commentCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
    private SpecStatus status;

    public Spec(User user, JobField jobField
            , Double educationScore
            , Double workExperienceScore
            , Double activityNetworkingScore
            , Double certificationScore
            , Double englishSkillScore, Double totalAnalysisScore, String assessment) {
        this.user = user;
        this.jobField = jobField;
        this.educationScore = educationScore;
        this.workExperienceScore = workExperienceScore;
        this.activityNetworkingScore = activityNetworkingScore;
        this.certificationScore = certificationScore;
        this.englishSkillScore = englishSkillScore;
        this.totalAnalysisScore = totalAnalysisScore;
        this.assessment = assessment;
        this.bookmarkCount = 0L;
        this.commentCount = 0L;
        this.status = SpecStatus.ACTIVE;
    }

    public static Spec createFromAiResponse(User user, JobField jobField, AiPostSpecResponse aiResponse) {
        return new Spec(
                user,
                jobField,
                aiResponse.getEducationScore(),
                aiResponse.getWorkExperienceScore(),
                aiResponse.getActivityNetworkingScore(),
                aiResponse.getCertificationScore(),
                aiResponse.getLanguageSkillScore(),
                aiResponse.getTotalScore(),
                aiResponse.getAssessment()
        );
    }

    @Override
    public void delete() {
        super.delete();
        this.status = SpecStatus.DELETED;
    }
}
