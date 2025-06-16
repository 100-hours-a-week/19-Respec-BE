package kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.mapping;

import java.util.List;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request.AiPostSpecRequest.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request.AiPostSpecRequest.LanguageSkill;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request.AiPostSpecRequest.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.response.AiPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.web.response.WebPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.request.PostSpecRequest;

public class AiDtoMapping {

    public static AiPostSpecRequest convertToSpecAnalysisRequest(PostSpecRequest request, String userNickname) {
        AiPostSpecRequest aiRequest = new AiPostSpecRequest();

        aiRequest.setNickname(userNickname);
        mappingFinalEducation(request, aiRequest);
        aiRequest.setJobField(request.getJobField());
        mappingEducationDetails(request, aiRequest);
        mappingWorkExperiences(request, aiRequest);
        mappingCertifications(request, aiRequest);
        mappingLanguageSkills(request, aiRequest);
        mappingActivities(request, aiRequest);

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

    public static WebPostResumeResponse.ResumeAnalysisResult convertToResumeAnalysisResponse(
            AiPostResumeResponse response) {
        WebPostResumeResponse.FinalEducation finalEducation =
                new WebPostResumeResponse.FinalEducation(response.getInstitute(), response.getFinalStatus());

        List<WebPostResumeResponse.EducationDetails> educationDetails =
                response.getEducationDetails().stream()
                        .map(e -> new WebPostResumeResponse.EducationDetails(
                                e.getSchoolName(),
                                e.getDegree(),
                                e.getMajor(),
                                e.getGpa(),
                                e.getMaxGpa()
                        )).toList();

        List<WebPostResumeResponse.WorkExperience> workExperiences =
                response.getWorkExperiences().stream()
                        .map(w -> new WebPostResumeResponse.WorkExperience(
                                w.getCompanyName(),
                                w.getPosition(),
                                w.getPeriod()
                        )).toList();

        List<WebPostResumeResponse.Certification> certifications =
                response.getCertificates().stream()
                        .map(name -> new WebPostResumeResponse.Certification(name))
                        .toList();

        List<WebPostResumeResponse.LanguageSkill> languageSkills =
                response.getLanguageSkills().stream()
                        .map(l -> new WebPostResumeResponse.LanguageSkill(
                                l.getLanguageTest(),
                                l.getScore()
                        )).toList();

        List<WebPostResumeResponse.Activity> activities =
                response.getActivities().stream()
                        .map(a -> new WebPostResumeResponse.Activity(
                                a.getName(),
                                a.getRole(),
                                a.getAward()
                        )).toList();

        return new WebPostResumeResponse.ResumeAnalysisResult(
                finalEducation,
                educationDetails,
                workExperiences,
                certifications,
                languageSkills,
                activities,
                response.getJobField()
        );
    }
}
