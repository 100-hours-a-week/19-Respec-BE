package kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.request;

import static java.util.Objects.requireNonNull;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.global.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.global.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.global.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.global.common.type.Position;

public record PostSpecRequest(
        @NotNull(message = "최종 학력 정보는 필수입니다")
        @Valid FinalEducation finalEducation,

        @NotNull(message = "null로 보낼 수 없습니다. 값이 없으면 빈 리스트로 보내십시오.")
        @Valid List<EducationDetail> educationDetails,

        @NotNull(message = "null로 보낼 수 없습니다. 값이 없으면 빈 리스트로 보내십시오.")
        @Valid List<WorkExperience> workExperiences,

        @NotNull(message = "null로 보낼 수 없습니다. 값이 없으면 빈 리스트로 보내십시오.")
        @Valid List<Certification> certifications,

        @NotNull(message = "null로 보낼 수 없습니다. 값이 없으면 빈 리스트로 보내십시오.")
        @Valid List<LanguageSkill> languageSkills,

        @NotNull(message = "null로 보낼 수 없습니다. 값이 없으면 빈 리스트로 보내십시오.")
        @Valid List<Activity> activities,

        @NotNull(message = "희망 직무 분야는 필수입니다")
        JobField jobField
) {
    public PostSpecRequest {
        educationDetails = List.copyOf(requireNonNull(educationDetails));
        workExperiences = List.copyOf(requireNonNull(workExperiences));
        certifications = List.copyOf(requireNonNull(certifications));
        languageSkills = List.copyOf(requireNonNull(languageSkills));
        activities = List.copyOf(requireNonNull(activities));
    }

    public record FinalEducation(
            @NotNull(message = "최종 학력 기관은 필수입니다") Institute institute,
            @NotNull(message = "최종 학력 상태는 필수입니다") FinalStatus status
    ) {
    }

    public record EducationDetail(
            @NotBlank(message = "학교 이름은 필수입니다") String schoolName,
            @NotNull(message = "학위 타입은 필수입니다") Degree degree,
            @NotBlank(message = "전공은 필수입니다") String major,
            @NotNull(message = "학점은 필수입니다")
            @PositiveOrZero(message = "학점은 0 이상이어야 합니다") Double gpa,
            @NotNull(message = "최대 학점은 필수입니다")
            @Positive(message = "최대 학점은 양수여야 합니다")
            @Max(value = 5, message = "최대 학점은 5 이하여야 합니다")
            @Min(value = 4, message = "최대 학점은 4 이상이어야 합니다") Double maxGpa
    ) {
    }

    public record WorkExperience(
            @NotBlank(message = "회사명은 필수입니다") String companyName,
            @NotNull(message = "직무는 필수입니다") Position position,
            @NotNull(message = "근무 기간은 필수입니다")
            @Positive(message = "근무 기간은 양수여야 합니다") Integer period
    ) {
    }

    public record Certification(
            @NotBlank(message = "자격증 이름은 필수입니다") String name
    ) {
    }

    public record LanguageSkill(
            @NotNull(message = "어학 시험 이름은 필수입니다") LanguageTest languageTest,
            @NotBlank(message = "점수는 필수입니다") String score
    ) {
    }

    public record Activity(
            @NotBlank(message = "활동 이름은 필수입니다") String name,
            @NotBlank(message = "역할은 필수입니다") String role,
            @NotNull(message = "수상내역은 필수입니다") String award
            // NotBlank가 아닌 NotNull이 맞습니다. 수상내역이 없을 경우 ""를 가져옵니다.
    ) {
    }
}
