package kakaotech.bootcamp.respec.specranking.domain.spec.dto.response;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Position;
import kakaotech.bootcamp.respec.specranking.domain.common.type.ScoreCategoryDetail;
import lombok.Data;

@Data
public class SpecDetailResponse {
    private Boolean isSuccess;
    private String message;
    private SpecDetailData specDetailData;

    @Data
    public static class SpecDetailData {
        private FinalEducation finalEducation;
        private List<EducationDetails> educationDetails;
        private List<WorkExperience> workExperiences;
        private List<Certification> certifications;
        private List<LanguageSkill> languageSkills;
        private List<Activity> activities;
        private JobField jobField;
        private Rankings rankings;
        private String portfolioUrl;
    }

    @Data
    public static class FinalEducation {
        private Institute institute;
        private FinalStatus finalStatus;
    }

    @Data
    public static class EducationDetails {
        private String schoolName;
        private Degree degree;
        private String major;
        private Double gpa;
        private Double maxGpa;
    }

    @Data
    public static class WorkExperience {
        private String company;
        private Position position;
        private Integer period;
    }

    @Data
    public static class Certification {
        private String name;
    }

    @Data
    public static class LanguageSkill {
        private LanguageTest name;
        private String score;
    }

    @Data
    public static class Activity {
        private String name;
        private String role;
        private String award;
    }

    @Data
    public static class Rankings {
        private Details details;
        private List<ScoreDetail> categories;
    }

    @Data
    public static class Details {
        private Double score;
        private Long jobFieldRank;
        private Long jobFieldUserCount;
        private Long TotalRank;
        private Long TotalUserCount;
    }

    @Data
    public static class ScoreDetail {
        private ScoreCategoryDetail name;
        private Double score;
    }

    public SpecDetailResponse(Boolean isSuccess, String message, SpecDetailData specDetailData) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.specDetailData = specDetailData;
    }
}
