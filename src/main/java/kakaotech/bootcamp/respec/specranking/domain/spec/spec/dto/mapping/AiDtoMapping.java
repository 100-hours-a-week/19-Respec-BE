package kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.mapping;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.spec.resume.dto.response.WebPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.resume.dto.response.WebPostResumeResponse.Certification;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.request.PostSpecRequest;
import kakaotech.bootcamp.respec.specranking.global.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.global.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.global.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.global.common.type.Position;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.request.AiPostSpecRequest.EducationDetail;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.request.AiPostSpecRequest.LanguageSkill;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.request.AiPostSpecRequest.WorkExperience;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.response.AiPostResumeResponse;

public class AiDtoMapping {

    public static AiPostSpecRequest convertToSpecAnalysisRequest(PostSpecRequest request, String userNickname) {
        AiPostSpecRequest aiRequest = new AiPostSpecRequest(userNickname,
                request.finalEducation().institute(), request.finalEducation().status(),
                request.jobField(),
                mappingEducationDetails(request),
                mappingWorkExperiences(request),
                mappingCertifications(request),
                mappingLanguageSkills(request),
                mappingActivities(request)
        );

        return aiRequest;
    }

    private static List<EducationDetail> mappingEducationDetails(PostSpecRequest request) {
        List<EducationDetail> universities = request.educationDetails().stream()
                .map(edu -> {
                    EducationDetail educationDetail = new EducationDetail(
                            edu.schoolName(), edu.degree(),
                            edu.major(), edu.gpa(), edu.maxGpa());
                    return educationDetail;
                })
                .collect(Collectors.toList());

        return universities;
    }

    private static List<WorkExperience> mappingWorkExperiences(PostSpecRequest request) {
        List<WorkExperience> workExperiences = request.workExperiences().stream()
                .map(work -> {
                    return new WorkExperience(work.companyName(), work.position(), work.period());
                })
                .collect(Collectors.toList());
        return workExperiences;
    }

    private static List<String> mappingCertifications(PostSpecRequest request) {
        List<String> certificates = request.certifications().stream()
                .map(cert -> cert.name())
                .collect(Collectors.toList());
        return certificates;
    }

    private static List<LanguageSkill> mappingLanguageSkills(PostSpecRequest request) {
        List<LanguageSkill> languageSkills = request.languageSkills().stream()
                .map(lang -> {
                    return new LanguageSkill(lang.languageTest(), lang.score());
                })
                .collect(Collectors.toList());
        return languageSkills;
    }

    private static List<AiPostSpecRequest.Activity> mappingActivities(PostSpecRequest request) {
        List<AiPostSpecRequest.Activity> activities = request.activities().stream()
                .map(act -> {
                    return new AiPostSpecRequest.Activity(
                            act.name(), act.role(), act.award());
                })
                .collect(Collectors.toList());
        return activities;
    }

    public static WebPostResumeResponse.ResumeAnalysisResult convertToResumeAnalysisResponse(
            AiPostResumeResponse response) {

        Institute institute = safeConvertToEnum(response.institute(), Institute.class, Institute.UNIVERSITY);
        FinalStatus finalStatus = safeConvertToEnum(response.finalStatus(), FinalStatus.class,
                FinalStatus.GRADUATED);
        JobField jobField = safeConvertToEnum(response.jobField(), JobField.class, JobField.INTERNET_IT);

        WebPostResumeResponse.FinalEducation finalEducation =
                new WebPostResumeResponse.FinalEducation(institute, finalStatus);

        List<WebPostResumeResponse.EducationDetails> educationDetails =
                response.educationDetails().stream()
                        .map(e -> {
                            Degree degree = safeConvertToEnum(e.degree(), Degree.class, Degree.BACHELOR);
                            return new WebPostResumeResponse.EducationDetails(
                                    e.schoolName(), degree,
                                    e.major(), e.gpa(), e.maxGpa());
                        }).toList();

        List<WebPostResumeResponse.WorkExperience> workExperiences =
                response.workExperiences().stream()
                        .map(w -> {
                            Position position = safeConvertToEnum(w.position(), Position.class,
                                    Position.INTERN);
                            return new WebPostResumeResponse.WorkExperience(
                                    w.companyName(), position, w.period());
                        }).toList();

        List<WebPostResumeResponse.Certification> certifications =
                response.certificates().stream()
                        .map(Certification::new)
                        .toList();

        List<WebPostResumeResponse.LanguageSkill> languageSkills =
                response.languageSkills().stream()
                        .map(l -> {
                            LanguageTest languageTest = safeConvertToEnum(l.languageTest(), LanguageTest.class,
                                    LanguageTest.TOEIC_ENGLISH);
                            return new WebPostResumeResponse.LanguageSkill(
                                    languageTest, l.score()
                            );
                        }).toList();

        List<WebPostResumeResponse.Activity> activities =
                response.activities().stream()
                        .map(a -> new WebPostResumeResponse.Activity(
                                a.name(), a.role(), a.award()
                        )).toList();

        return new WebPostResumeResponse.ResumeAnalysisResult(
                finalEducation, educationDetails,
                workExperiences, certifications,
                languageSkills, activities, jobField
        );
    }


    private static <T extends Enum<T>> T safeConvertToEnum(String value, Class<T> enumClass, T defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            return Arrays.stream(enumClass.getEnumConstants())
                    .filter(enumConstant -> {
                        try {
                            String enumValue = (String) enumConstant.getClass().getMethod("getValue")
                                    .invoke(enumConstant);
                            return enumValue.equals(value);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .findFirst()
                    .orElse(defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
