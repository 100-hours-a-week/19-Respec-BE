package kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.global.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.global.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.global.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.global.common.type.Position;
import kakaotech.bootcamp.respec.specranking.global.common.type.ScoreCategoryDetail;

public record SpecDetailResponse(
        Boolean isSuccess,
        String message,
        SpecDetailData specDetailData
) {

    public record SpecDetailData(
            FinalEducation finalEducation,
            List<EducationDetails> educationDetails,
            List<WorkExperience> workExperiences,
            List<Certification> certifications,
            List<LanguageSkill> languageSkills,
            List<Activity> activities,
            JobField jobField,
            Rankings rankings,
            String assessment
    ) {
        public SpecDetailData {
            educationDetails = List.copyOf(educationDetails);
            workExperiences = List.copyOf(workExperiences);
            certifications = List.copyOf(certifications);
            languageSkills = List.copyOf(languageSkills);
            activities = List.copyOf(activities);
        }
    }

    public record FinalEducation(
            Institute institute,
            FinalStatus finalStatus
    ) {
    }

    public record EducationDetails(
            String schoolName,
            Degree degree,
            String major,
            Double gpa,
            Double maxGpa
    ) {
    }

    public record WorkExperience(
            String company,
            Position position,
            Integer period
    ) {
    }

    public record Certification(
            String name
    ) {
    }

    public record LanguageSkill(
            LanguageTest name,
            String score
    ) {
    }

    public record Activity(
            String name,
            String role,
            String award
    ) {
    }

    public record Rankings(
            Details details,
            List<ScoreDetail> categories
    ) {
        public Rankings {
            categories = List.copyOf(categories);
        }
    }

    public record Details(
            Double score,
            Long jobFieldRank,
            Long jobFieldUserCount,
            Long totalRank,
            Long totalUserCount
    ) {
    }

    public record ScoreDetail(
            ScoreCategoryDetail name,
            Double score
    ) {
    }
}
