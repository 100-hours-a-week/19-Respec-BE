package kakaotech.bootcamp.respec.specranking.domain.spec.dto.response;

import lombok.Data;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecCategory;

@Data
public class SpecDetailResponse {
    private boolean isSuccess;
    private String message;
    private SpecDetailData data;

    @Data
    public static class SpecDetailData {
        private Long specId;
        private FinalEducation finalEducation;
        private List<Education> educations;
        private List<WorkExperience> workExperience;
        private List<Certification> certifications;
        private List<LanguageSkill> languageSkills;
        private List<Activity> activities;
        private String jobField;
        private Rankings rankings;
        private String portfolioUrl;
    }

    @Data
    public static class FinalEducation {
        private String level;
        private String status;
    }

    @Data
    public static class Education {
        private String schoolName;
        private String degree;
        private String major;
        private Double gpa;
        private Double maxGpa;
    }

    @Data
    public static class WorkExperience {
        private String company;
        private String position;
        private Integer period;
    }

    @Data
    public static class Certification {
        private String name;
    }

    @Data
    public static class LanguageSkill {
        private String name;
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
        private Overall overall;
        private List<Category> categories;
    }

    @Data
    public static class Overall {
        private Double score;
        private Integer totalUserCount;
        private Integer rank;
    }

    @Data
    public static class Category {
        private SpecCategory name;
        private Double score;
    }

    public SpecDetailResponse(boolean isSuccess, String message, SpecDetailData data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public static SpecDetailResponse success(SpecDetailData data) {
        return new SpecDetailResponse(true, "스펙 정보 조회 성공", data);
    }
}
