package kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.request;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.global.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.global.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.global.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.global.common.type.Position;

public record AiPostSpecRequest(
        String nickname,

        @JsonProperty("final_edu")
        Institute institute,

        @JsonProperty("final_status")
        FinalStatus finalStatus,

        @JsonProperty("desired_job")
        JobField jobField,

        @JsonProperty("universities")
        List<EducationDetail> educationDetails,

        @JsonProperty("careers")
        List<WorkExperience> workExperiences,

        List<String> certificates,

        @JsonProperty("languages")
        List<LanguageSkill> languageSkills,

        List<Activity> activities
) {
    public AiPostSpecRequest {
        educationDetails = List.copyOf(requireNonNull(educationDetails));
        workExperiences = List.copyOf(requireNonNull(workExperiences));
        certificates = List.copyOf(requireNonNull(certificates));
        languageSkills = List.copyOf(requireNonNull(languageSkills));
        activities = List.copyOf(requireNonNull(activities));
    }

    public record EducationDetail(
            @JsonProperty("school_name")
            String schoolName,
            Degree degree,
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
            Position position,
            @JsonProperty("work_month")
            Integer period
    ) {
    }

    public record LanguageSkill(
            @JsonProperty("test")
            LanguageTest languageTest,
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
