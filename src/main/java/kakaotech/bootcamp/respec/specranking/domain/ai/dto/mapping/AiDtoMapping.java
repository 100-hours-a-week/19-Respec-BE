package kakaotech.bootcamp.respec.specranking.domain.ai.dto.mapping;

import java.util.List;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest.LanguageSkill;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.request.PostSpecRequest;

public class AiDtoMapping {

    public static AiPostSpecRequest convertToAiRequest(PostSpecRequest request, String userNickname,
                                                       String portfolioUrl) {
        AiPostSpecRequest aiRequest = new AiPostSpecRequest();

        aiRequest.setNickname(userNickname);
        mappingFinalEducation(request, aiRequest);
        aiRequest.setJobField(request.getJobField());
        mappingEducationDetails(request, aiRequest);
        mappingWorkExperiences(request, aiRequest);
        mappingCertifications(request, aiRequest);
        mappingLanguageSkills(request, aiRequest);
        mappingActivities(request, aiRequest);
        aiRequest.setPortfolioURL(portfolioUrl);

        return aiRequest;
    }

    private static void mappingFinalEducation(PostSpecRequest request, AiPostSpecRequest aiRequest) {
        aiRequest.setInstitute(request.getFinalEducation().getInstitute());
        aiRequest.setFinalStatus(request.getFinalEducation().getStatus());
    }

    private static void mappingEducationDetails(PostSpecRequest request, AiPostSpecRequest aiRequest) {
        List<EducationDetail> universities = request.getEducationDetails().stream()
                .map(edu -> {
                    EducationDetail educationDetail = new EducationDetail();
                    educationDetail.setSchoolName(edu.getSchoolName());
                    educationDetail.setDegree(edu.getDegree());
                    educationDetail.setMajor(edu.getMajor());
                    educationDetail.setGpa(edu.getGpa());
                    educationDetail.setMaxGpa(edu.getMaxGpa());
                    return educationDetail;
                })
                .collect(Collectors.toList());
        aiRequest.setEducationDetails(universities);
    }

    private static void mappingWorkExperiences(PostSpecRequest request, AiPostSpecRequest aiRequest) {
        List<WorkExperience> workExperiences = request.getWorkExperiences().stream()
                .map(work -> {
                    WorkExperience workExperience = new WorkExperience();
                    workExperience.setCompanyName(work.getCompanyName());
                    workExperience.setPosition(work.getPosition());
                    workExperience.setPeriod(work.getPeriod());
                    return workExperience;
                })
                .collect(Collectors.toList());
        aiRequest.setWorkExperiences(workExperiences);
    }

    private static void mappingCertifications(PostSpecRequest request, AiPostSpecRequest aiRequest) {
        List<String> certificates = request.getCertifications().stream()
                .map(cert -> cert.getName())
                .collect(Collectors.toList());
        aiRequest.setCertificates(certificates);
    }

    private static void mappingLanguageSkills(PostSpecRequest request, AiPostSpecRequest aiRequest) {
        List<LanguageSkill> languageSkills = request.getLanguageSkills().stream()
                .map(lang -> {
                    LanguageSkill languageSkill = new LanguageSkill();
                    languageSkill.setLanguageTest(lang.getLanguageTest());
                    languageSkill.setScore(lang.getScore());
                    return languageSkill;
                })
                .collect(Collectors.toList());
        aiRequest.setLanguageSkills(languageSkills);
    }

    private static void mappingActivities(PostSpecRequest request, AiPostSpecRequest aiRequest) {
        List<AiPostSpecRequest.Activity> activities = request.getActivities().stream()
                .map(act -> {
                    AiPostSpecRequest.Activity activity = new AiPostSpecRequest.Activity();
                    activity.setName(act.getName());
                    activity.setRole(act.getRole());
                    activity.setAward(act.getAward());
                    return activity;
                })
                .collect(Collectors.toList());
        aiRequest.setActivities(activities);
    }
    
}
