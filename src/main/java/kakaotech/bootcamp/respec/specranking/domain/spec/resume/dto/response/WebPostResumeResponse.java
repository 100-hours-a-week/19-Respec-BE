package kakaotech.bootcamp.respec.specranking.domain.spec.resume.dto.response;

import static java.util.Objects.requireNonNull;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.global.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.global.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.global.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.global.common.type.Position;

public record WebPostResumeResponse(
        Boolean isSuccess,
        String message,
        ResumeAnalysisResult data
) {
    public record ResumeAnalysisResult(
            FinalEducation finalEducation,
            List<EducationDetails> educationDetails,
            List<WorkExperience> workExperiences,
            List<Certification> certifications,
            List<LanguageSkill> languageSkills,
            List<Activity> activities,
            JobField jobField
    ) {
        public ResumeAnalysisResult {
            educationDetails = List.copyOf(requireNonNull(educationDetails));
            workExperiences = List.copyOf(requireNonNull(workExperiences));
            certifications = List.copyOf(requireNonNull(certifications));
            languageSkills = List.copyOf(requireNonNull(languageSkills));
            activities = List.copyOf(requireNonNull(activities));
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
}
