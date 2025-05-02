package kakaotech.bootcamp.respec.specranking.domain.ai.dto.mapping;

import java.util.List;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest.University;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.request.PostSpecRequest;

public class AiDtoMapping {

    public static AiPostSpecRequest convertToAiRequest(PostSpecRequest request, String userNickname,
                                                       String portfolioUrl) {
        AiPostSpecRequest aiRequest = new AiPostSpecRequest();

        aiRequest.setDesiredJob(request.getJobField());
        aiRequest.setFilelink(portfolioUrl);
        aiRequest.setNickname(userNickname);

        if (request.getFinalEducation() != null) {
            aiRequest.setFinalEdu(request.getFinalEducation().getLevel());
            aiRequest.setFinalStatus(request.getFinalEducation().getStatus());
        }

        if (request.getEducations() != null && !request.getEducations().isEmpty()) {
            List<University> universities = request.getEducations().stream()
                    .map(edu -> {
                        AiPostSpecRequest.University university = new AiPostSpecRequest.University();
                        university.setSchoolName(edu.getSchoolName());
                        university.setDegree(edu.getDegree());
                        university.setMajor(edu.getMajor());
                        university.setGpa(edu.getGpa());
                        university.setGpaMax(edu.getMaxGpa());
                        return university;
                    })
                    .collect(Collectors.toList());
            aiRequest.setUniversities(universities);
        }

        if (request.getWorkExperience() != null && !request.getWorkExperience().isEmpty()) {
            List<AiPostSpecRequest.Career> careers = request.getWorkExperience().stream()
                    .map(work -> {
                        AiPostSpecRequest.Career career = new AiPostSpecRequest.Career();
                        career.setCompany(work.getCompany());
                        career.setRole(work.getPosition());
                        return career;
                    })
                    .collect(Collectors.toList());
            aiRequest.setCareers(careers);
        }

        if (request.getCertifications() != null && !request.getCertifications().isEmpty()) {
            List<String> certificates = request.getCertifications().stream()
                    .map(cert -> cert.getName())
                    .collect(Collectors.toList());
            aiRequest.setCertificates(certificates);
        }

        if (request.getLanguageSkills() != null && !request.getLanguageSkills().isEmpty()) {
            List<AiPostSpecRequest.Language> languages = request.getLanguageSkills().stream()
                    .map(lang -> {
                        AiPostSpecRequest.Language language = new AiPostSpecRequest.Language();
                        language.setTest(lang.getName());
                        language.setScoreOrGrade(lang.getScore());
                        return language;
                    })
                    .collect(Collectors.toList());
            aiRequest.setLanguages(languages);
        }

        if (request.getActivities() != null && !request.getActivities().isEmpty()) {
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

        return aiRequest;
    }
}
