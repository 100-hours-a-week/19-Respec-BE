package kakaotech.bootcamp.respec.specranking.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.CareerRole;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalEducation;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AiPostSpecRequest {

    private String nickname;

    @JsonProperty("final_edu")
    private FinalEducation finalEdu;

    @JsonProperty("final_status")
    private FinalStatus finalStatus;

    @JsonProperty("desired_job")
    private String desiredJob;

    private List<University> universities;

    private List<Career> careers;

    private List<String> certificates;

    private List<Language> languages;

    private List<Activity> activities;

    @JsonIgnore
    private String filelink;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class University {
        @JsonProperty("school_name")
        private String schoolName;

        private Degree degree;

        private String major;

        private Double gpa;

        @JsonProperty("gpa_max")
        private Double gpaMax;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Career {
        private String company;
        private CareerRole role;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Language {
        private String test;

        @JsonProperty("score_or_grade")
        private String scoreOrGrade;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Activity {
        private String name;
        private String role;
        private String award;
    }
}