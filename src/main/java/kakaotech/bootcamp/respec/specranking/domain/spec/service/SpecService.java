package kakaotech.bootcamp.respec.specranking.domain.spec.service;

import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;
import kakaotech.bootcamp.respec.specranking.domain.ai.service.AiService;
import kakaotech.bootcamp.respec.specranking.domain.common.type.CareerRole;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalEducation;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.request.PostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.ActivityNetworking;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Certification;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Education;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.EnglishSkill;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.ActivityNetworkingRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.CertificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.EducationDetailRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.EducationRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.EnglishSkillRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.WorkExperienceRepository;
import kakaotech.bootcamp.respec.specranking.domain.store.FileStore;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.util.GetCurrentUserService;
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
    private final EnglishSkillRepository englishSkillRepository;
    private final ActivityNetworkingRepository activityNetworkingRepository;
    private final GetCurrentUserService getCurrentUserService;
    private final FileStore fileStore;

    public void createSpec(PostSpecRequest request, MultipartFile portfolioFile) {
        Long userId = getCurrentUserService.getUserId();

        Optional<Spec> existingSpec = specRepository.findByUserId(userId);
        if (existingSpec.isPresent()) {
            throw new IllegalStateException("이미 등록된 스펙이 있습니다. 스펙을 수정하려면 수정 API를 사용해주세요.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. ID: " + userId));

        String portfolioUrl = fileStore.upload(portfolioFile);

        AiPostSpecRequest aiPostSpecRequest = aiService.convertToAiRequest(request, portfolioUrl);
        AiPostSpecResponse aiPostSpecResponse = aiService.analyzeSpec(aiPostSpecRequest);

        Spec spec = Spec.createFromAiResponse(user, request.getJobField(), aiPostSpecResponse);
        Spec savedSpec = specRepository.save(spec);

        saveEducation(savedSpec, request);
        saveWorkExperience(savedSpec, request);
        saveCertifications(savedSpec, request);
        saveLanguageSkills(savedSpec, request);
        saveActivities(savedSpec, request);
    }

    private void saveEducation(Spec spec, PostSpecRequest request) {
        if (request.getFinalEducation() != null) {
            FinalStatus institute = request.getFinalEducation().getStatus();
            FinalEducation status = request.getFinalEducation().getLevel();

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
                EnglishSkill englishSkill = new EnglishSkill(
                        spec,
                        languageSkillDto.getName(),
                        "English",
                        languageSkillDto.getScore()
                );

                englishSkillRepository.save(englishSkill);
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

    public void updateSpec(Long specId, PostSpecRequest request, MultipartFile portfolioFile) {
        Long userId = getCurrentUserService.getUserId();

        Spec spec = specRepository.findById(specId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스펙입니다. ID: " + specId));

        if (!spec.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 스펙에 대한 수정 권한이 없습니다.");
        }

        spec.sleep();

        String portfolioUrl = portfolioFile != null ? fileStore.upload(portfolioFile) : null;

        AiPostSpecRequest aiPostSpecRequest = aiService.convertToAiRequest(request, portfolioUrl);
        AiPostSpecResponse aiPostSpecResponse = aiService.analyzeSpec(aiPostSpecRequest);

        User user = spec.getUser();
        Spec newSpec = Spec.createFromAiResponse(user, request.getJobField(), aiPostSpecResponse);
        Spec savedNewSpec = specRepository.save(newSpec);

        saveEducation(savedNewSpec, request);
        saveWorkExperience(savedNewSpec, request);
        saveCertifications(savedNewSpec, request);
        saveLanguageSkills(savedNewSpec, request);
        saveActivities(savedNewSpec, request);
    }


}
