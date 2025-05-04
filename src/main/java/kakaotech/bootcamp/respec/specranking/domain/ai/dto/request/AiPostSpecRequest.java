package kakaotech.bootcamp.respec.specranking.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Position;
import lombok.Data;

@Data
public class AiPostSpecRequest {

    private String nickname;

    @JsonProperty("final_edu")
    private Institute institute;

    @JsonProperty("final_status")
    private FinalStatus finalStatus;

    @JsonProperty("desired_job")
    private JobField jobField;

    @JsonProperty("universities")
    private List<EducationDetail> educationDetails;

    @JsonProperty("careers")
    private List<WorkExperience> workExperiences;

    private List<String> certificates;

    @JsonProperty("languages")
    private List<LanguageSkill> languageSkills;

    private List<Activity> activities;

    @JsonIgnore
    @JsonProperty("filelink")
    private String portfolioURL;

    @Data
    public static class EducationDetail {
        @JsonProperty("school_name")
        private String schoolName;

        private Degree degree;

        private String major;

        private Double gpa;

        @JsonProperty("gpa_max")
        private Double maxGpa;
    }

    @Data
    public static class WorkExperience {
        @JsonProperty("company")
        private String companyName;
        @JsonProperty("role")
        private Position position;
    }

    @Data
    public static class LanguageSkill {
        @JsonProperty("test")
        private LanguageTest languageTest;

        @JsonProperty("score_or_grade")
        private String score;
    }

    @Data
    public static class Activity {
        private String name;
        private String role;
        private String award;
    }
}