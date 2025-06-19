package kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AiPostResumeResponse {
    @JsonProperty("final_edu")
    private final String institute;

    @JsonProperty("final_status")
    private final String finalStatus;

    @JsonProperty("desired_job")
    private final String jobField;

    @JsonProperty("universities")
    private final List<EducationDetail> educationDetails;

    @JsonProperty("careers")
    private final List<WorkExperience> workExperiences;

    private final List<String> certificates;

    @JsonProperty("languages")
    private final List<LanguageSkill> languageSkills;

    private final List<Activity> activities;

    @Getter
    @RequiredArgsConstructor
    public static class EducationDetail {
        @JsonProperty("school_name")
        private final String schoolName;

        private final String degree;

        private final String major;

        private final Double gpa;

        @JsonProperty("gpa_max")
        private final Double maxGpa;

    }

    @Getter
    @RequiredArgsConstructor
    public static class WorkExperience {
        @JsonProperty("company")
        private final String companyName;
        @JsonProperty("role")
        private final String position;
        @JsonProperty("work_month")
        private final Integer period;
    }

    @Getter
    @RequiredArgsConstructor
    public static class LanguageSkill {
        @JsonProperty("test")
        private final String languageTest;

        @JsonProperty("score_or_grade")
        private final String score;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Activity {
        private final String name;
        private final String role;
        private final String award;
    }
}
