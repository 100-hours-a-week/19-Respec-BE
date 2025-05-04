package kakaotech.bootcamp.respec.specranking.domain.spec.service;

import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.activitynetworking.entity.ActivityNetworking;
import kakaotech.bootcamp.respec.specranking.domain.activitynetworking.repository.ActivityNetworkingRepository;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.mapping.AiDtoMapping;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;
import kakaotech.bootcamp.respec.specranking.domain.ai.service.AiService;
import kakaotech.bootcamp.respec.specranking.domain.certification.entity.Certification;
import kakaotech.bootcamp.respec.specranking.domain.certification.repository.CertificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.common.type.CareerRole;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.domain.education.entity.Education;
import kakaotech.bootcamp.respec.specranking.domain.education.repository.EducationRepository;
import kakaotech.bootcamp.respec.specranking.domain.educationdetail.entity.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.educationdetail.repository.EducationDetailRepository;
import kakaotech.bootcamp.respec.specranking.domain.languageskill.entity.LanguageSkill;
import kakaotech.bootcamp.respec.specranking.domain.languageskill.repository.LanguageSkillRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.request.PostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.store.service.FileStore;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.domain.workexperience.entity.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.workexperience.repository.WorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class SpecService {

    private final AiService aiService;
    private final UserRepository userRepository;
    private final SpecRepository specRepository;
    private final EducationRepository educationRepository;
    private final EducationDetailRepository educationDetailRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final CertificationRepository certificationRepository;
    private final LanguageSkillRepository languageSkillRepository;
    private final ActivityNetworkingRepository activityNetworkingRepository;
    private final FileStore fileStore;

    public void createSpec(PostSpecRequest request, MultipartFile portfolioFile) {
        Long userId = UserUtils.getCurrentUserId();
        validateMultipleSpec(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. ID: " + userId));

        String portfolioUrl = "";
        if (portfolioFile != null) {
            portfolioUrl = fileStore.upload(portfolioFile);
        }

        AiPostSpecRequest aiPostSpecRequest = AiDtoMapping.convertToAiRequest(request, user.getNickname(),
                portfolioUrl);
        AiPostSpecResponse aiPostSpecResponse = aiService.analyzeSpec(aiPostSpecRequest);

        saveSpecWithChaining(request, aiPostSpecResponse, user);
    }

    public void updateSpec(Long specId, PostSpecRequest request, MultipartFile portfolioFile) {
        Long userId = UserUtils.getCurrentUserId();

        Spec spec = specRepository.findById(specId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스펙입니다. ID: " + specId));

        if (!spec.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 스펙에 대한 수정 권한이 없습니다.");
        }

        String portfolioUrl = "";
        if (portfolioFile != null) {
            portfolioUrl = fileStore.upload(portfolioFile);
        }

        AiPostSpecRequest aiPostSpecRequest = AiDtoMapping.convertToAiRequest(request, spec.getUser().getNickname(),
                portfolioUrl);
        AiPostSpecResponse aiPostSpecResponse = aiService.analyzeSpec(aiPostSpecRequest);

        User user = spec.getUser();
        spec.delete();
        saveSpecWithChaining(request, aiPostSpecResponse, user);
    }

    private void validateMultipleSpec(Long userId) {
        Optional<Spec> existingSpec = specRepository.findByUserId(userId);
        if (existingSpec.isPresent()) {
            throw new IllegalStateException("이미 등록된 스펙이 있습니다. 스펙을 수정하려면 수정 API를 사용해주세요.");
        }
    }

    private void saveSpecWithChaining(PostSpecRequest request, AiPostSpecResponse aiPostSpecResponse, User user) {
        Spec newSpec = Spec.createFromAiResponse(user, request.getJobField(), aiPostSpecResponse);
        Spec savedNewSpec = specRepository.save(newSpec);

        saveEducation(savedNewSpec, request);
        saveWorkExperience(savedNewSpec, request);
        saveCertifications(savedNewSpec, request);
        saveLanguageSkills(savedNewSpec, request);
        saveActivities(savedNewSpec, request);
    }

    private void saveEducation(Spec spec, PostSpecRequest request) {
        if (request.getFinalEducation() != null) {
            FinalStatus institute = request.getFinalEducation().getStatus();
            Institute status = request.getFinalEducation().getInstitute();

            Education education = new Education(spec, institute, status);
            Education savedEducation = educationRepository.save(education);

            if (request.getEducations() != null && !request.getEducations().isEmpty()) {
                for (PostSpecRequest.Education educationDto : request.getEducations()) {
                    Degree degree = educationDto.getDegree();

                    EducationDetail educationDetail = new EducationDetail(
                            savedEducation,
                            educationDto.getSchoolName(),
                            degree,
                            educationDto.getMajor(),
                            educationDto.getGpa(),
                            educationDto.getMaxGpa()
                    );

                    educationDetailRepository.save(educationDetail);
                }
            }
        }
    }

    private void saveWorkExperience(Spec spec, PostSpecRequest request) {
        if (request.getWorkExperience() != null && !request.getWorkExperience().isEmpty()) {
            for (PostSpecRequest.WorkExperience workExp : request.getWorkExperience()) {
                CareerRole position = workExp.getPosition();

                WorkExperience workExperience = new WorkExperience(
                        spec,
                        workExp.getCompany(),
                        position,
                        workExp.getPeriod()
                );

                workExperienceRepository.save(workExperience);
            }
        }
    }

    private void saveCertifications(Spec spec, PostSpecRequest request) {
        if (request.getCertifications() != null && !request.getCertifications().isEmpty()) {
            for (PostSpecRequest.Certification certificationDto : request.getCertifications()) {
                Certification certification = new Certification(
                        spec,
                        certificationDto.getName()
                );

                certificationRepository.save(certification);
            }
        }
    }

    private void saveLanguageSkills(Spec spec, PostSpecRequest request) {
        if (request.getLanguageSkills() != null && !request.getLanguageSkills().isEmpty()) {
            for (PostSpecRequest.LanguageSkill languageSkillDto : request.getLanguageSkills()) {
                LanguageSkill languageSkill = new LanguageSkill(
                        spec,
                        languageSkillDto.getLanguageTest(),
                        languageSkillDto.getScore()
                );

                languageSkillRepository.save(languageSkill);
            }
        }
    }

    private void saveActivities(Spec spec, PostSpecRequest request) {
        if (request.getActivities() != null && !request.getActivities().isEmpty()) {
            for (PostSpecRequest.Activity activityDto : request.getActivities()) {
                ActivityNetworking activity = new ActivityNetworking(
                        spec,
                        activityDto.getName(),
                        activityDto.getRole(),
                        activityDto.getAward()
                );

                activityNetworkingRepository.save(activity);
            }
        }
    }

}
