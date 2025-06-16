package kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Position;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AiPostResumeResponse {
    @JsonProperty("final_edu")
    private final Institute institute;

    @JsonProperty("final_status")
    private final FinalStatus finalStatus;

    @JsonProperty("desired_job")
    private final JobField jobField;

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

        private final Degree degree;

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
        private final Position position;
        @JsonProperty("work_month")
        private final Integer period;
    }

    @Getter
    @RequiredArgsConstructor
    public static class LanguageSkill {
        @JsonProperty("test")
        private final LanguageTest languageTest;

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
