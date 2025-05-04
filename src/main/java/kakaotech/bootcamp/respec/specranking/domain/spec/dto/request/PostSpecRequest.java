package kakaotech.bootcamp.respec.specranking.domain.spec.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Position;
import lombok.Data;

@Data
public class PostSpecRequest {
    @NotNull(message = "최종 학력 정보는 필수입니다")
    @Valid
    private FinalEducation finalEducation;
    @Valid
    private List<EducationDetail> educationDetails = new ArrayList<>();
    @Valid
    private List<WorkExperience> workExperiences = new ArrayList<>();
    @Valid
    private List<Certification> certifications = new ArrayList<>();
    @Valid
    private List<LanguageSkill> languageSkills = new ArrayList<>();
    @Valid
    private List<Activity> activities = new ArrayList<>();
    @NotNull(message = "희망 직무 분야는 필수입니다")
    private JobField jobField;

    @Data
    public static class FinalEducation {
        @NotNull(message = "최종 학력 기관은 필수입니다")
        private Institute institute;

        @NotNull(message = "최종 학력 상태는 필수입니다")
        private FinalStatus status;
    }

    @Data
    public static class EducationDetail {
        @NotBlank(message = "학교 이름은 필수입니다")
        private String schoolName;
        @NotNull(message = "학위 타입은 필수입니다")
        private Degree degree;
        @NotBlank(message = "전공은 필수입니다")
        private String major;
        @NotNull(message = "학점은 필수입니다")
        @PositiveOrZero(message = "학점은 0 이상이어야 합니다")
        private Double gpa;
        @NotNull(message = "최대 학점은 필수입니다")
        @Positive(message = "최대 학점은 양수여야 합니다")
        @Max(value = 5, message = "최대 학점은 5 이하여야 합니다")
        @Min(value = 4, message = "최대 학점은 4 이상이어야 합니다")
        private Double maxGpa;
    }

    @Data
    public static class WorkExperience {
        @NotBlank(message = "회사명은 필수입니다")
        private String companyName;

        @NotNull(message = "직무는 필수입니다")
        private Position position;

        @NotNull(message = "근무 기간은 필수입니다")
        @Positive(message = "근무 기간은 양수여야 합니다")
        private Integer period;
    }

    @Data
    public static class Certification {
        @NotBlank(message = "자격증 이름은 필수입니다")
        private String name;
    }

    @Data
    public static class LanguageSkill {
        @NotNull(message = "어학 시험 이름은 필수입니다")
        private LanguageTest languageTest;

        @NotBlank(message = "점수는 필수입니다")
        private String score;
    }

    @Data
    public static class Activity {
        @NotBlank(message = "활동 이름은 필수입니다")
        private String name;

        @NotBlank(message = "역할은 필수입니다")
        private String role;

        @NotNull // NotBlank가 아닌 NotNull이 맞습니다. 수상내역이 없을 경우 ""를 가져옵니다.
        private String award;
    }
}