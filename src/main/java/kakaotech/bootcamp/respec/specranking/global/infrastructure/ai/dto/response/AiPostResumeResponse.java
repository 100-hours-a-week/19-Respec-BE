package kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.response;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AiPostResumeResponse(
        @JsonProperty("final_edu")
        String institute,

        @JsonProperty("final_status")
        String finalStatus,

        @JsonProperty("desired_job")
        String jobField,

        @JsonProperty("universities")
        List<EducationDetail> educationDetails,

        @JsonProperty("careers")
        List<WorkExperience> workExperiences,

        List<String> certificates,

        @JsonProperty("languages")
        List<LanguageSkill> languageSkills,

        List<Activity> activities
) {
    public AiPostResumeResponse {
        educationDetails = List.copyOf(requireNonNull(educationDetails));
        workExperiences = List.copyOf(requireNonNull(workExperiences));
        certificates = List.copyOf(requireNonNull(certificates));
        languageSkills = List.copyOf(requireNonNull(languageSkills));
        activities = List.copyOf(requireNonNull(activities));
    }

    public record EducationDetail(
            @JsonProperty("school_name")
            String schoolName,
            String degree,
            String major,
            Double gpa,
            @JsonProperty("gpa_max")
            Double maxGpa
    ) {
    }

    public record WorkExperience(
            @JsonProperty("company")
            String companyName,
            @JsonProperty("role")
            String position,
            @JsonProperty("work_month")
            Integer period
    ) {
    }

    public record LanguageSkill(
            @JsonProperty("test")
            String languageTest,
            @JsonProperty("score_or_grade")
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
