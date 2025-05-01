package kakaotech.bootcamp.respec.specranking.domain.spec.dto.request;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.CareerRole;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import lombok.Data;

@Data
public class PostSpecRequest {
    private FinalEducation finalEducation;
    private List<Education> educations;
    private List<WorkExperience> workExperience;
    private List<Certification> certifications;
    private List<LanguageSkill> languageSkills;
    private List<Activity> activities;
    private String jobField;

    @Data
    public static class FinalEducation {
        private kakaotech.bootcamp.respec.specranking.domain.common.type.FinalEducation level;
        private FinalStatus status;
    }

    @Data
    public static class Education {
        private String schoolName;
        private Degree degree;
        private String major;
        private Double gpa;
        private Double maxGpa;
    }

    @Data
    public static class WorkExperience {
        private String company;
        private CareerRole position;
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
}