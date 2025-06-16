package kakaotech.bootcamp.respec.specranking.domain.spec.service;

import java.util.List;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.activitynetworking.entity.ActivityNetworking;
import kakaotech.bootcamp.respec.specranking.domain.activitynetworking.repository.ActivityNetworkingRepository;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.mapping.AiDtoMapping;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.response.AiPostSpecResponse;
import kakaotech.bootcamp.respec.specranking.domain.ai.service.AiService;
import kakaotech.bootcamp.respec.specranking.domain.certification.entity.Certification;
import kakaotech.bootcamp.respec.specranking.domain.certification.repository.CertificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Position;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.domain.education.entity.Education;
import kakaotech.bootcamp.respec.specranking.domain.education.repository.EducationRepository;
import kakaotech.bootcamp.respec.specranking.domain.educationdetail.repository.EducationDetailRepository;
import kakaotech.bootcamp.respec.specranking.domain.languageskill.entity.LanguageSkill;
import kakaotech.bootcamp.respec.specranking.domain.languageskill.repository.LanguageSkillRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.request.PostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.request.PostSpecRequest.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.domain.workexperience.entity.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.workexperience.repository.WorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void createSpec(PostSpecRequest request) {
        Optional<Long> userIdOpt = UserUtils.getCurrentUserId();
        Long userId = userIdOpt.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. ID: " + userId));

        validateMultipleSpec(userId);

        AiPostSpecRequest aiPostSpecRequest = AiDtoMapping.convertToSpecAnalysisRequest(request, user.getNickname());
        AiPostSpecResponse aiPostSpecResponse = aiService.analyzeSpec(aiPostSpecRequest);

        saveSpecWithChaining(request, aiPostSpecResponse, user);
    }

    public void updateSpec(Long specId, PostSpecRequest request) {
        Optional<Long> userIdOpt = UserUtils.getCurrentUserId();
        Long userId = userIdOpt.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. ID: " + userId));

        Spec spec = specRepository.findByIdAndStatus(specId, SpecStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("수정할 수 없는 스펙입니다. ID: " + specId));

        if (!spec.getUser().equals(user)) {
            throw new IllegalArgumentException("해당 스펙에 대한 수정 권한이 없습니다.");
        }

        AiPostSpecRequest aiPostSpecRequest = AiDtoMapping.convertToSpecAnalysisRequest(request, user.getNickname());
        AiPostSpecResponse aiPostSpecResponse = aiService.analyzeSpec(aiPostSpecRequest);

        spec.delete();
        saveSpecWithChaining(request, aiPostSpecResponse, user);
    }

    private void validateMultipleSpec(Long userId) {
        Optional<Spec> existingActiveSpec = specRepository.findByUserIdAndStatus(userId, SpecStatus.ACTIVE);
        if (existingActiveSpec.isPresent()) {
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
        if (isNotEmpty(request.getFinalEducation())) {
            FinalStatus status = request.getFinalEducation().getStatus();
            Institute institute = request.getFinalEducation().getInstitute();

            Education education = new Education(spec, institute, status);
            Education savedEducation = educationRepository.save(education);

            if (isNotEmpty(request.getEducationDetails())) {
                for (EducationDetail educationDetailDto : request.getEducationDetails()) {
                    Degree degree = educationDetailDto.getDegree();

                    kakaotech.bootcamp.respec.specranking.domain.educationdetail.entity.EducationDetail educationDetail = new kakaotech.bootcamp.respec.specranking.domain.educationdetail.entity.EducationDetail(
                            savedEducation,
                            educationDetailDto.getSchoolName(),
                            degree,
                            educationDetailDto.getMajor(),
                            educationDetailDto.getGpa(),
                            educationDetailDto.getMaxGpa()
                    );

                    educationDetailRepository.save(educationDetail);
                }
            }
        }
    }

    private void saveWorkExperience(Spec spec, PostSpecRequest request) {
        if (isNotEmpty(request.getWorkExperiences())) {
            for (PostSpecRequest.WorkExperience workExp : request.getWorkExperiences()) {
                Position position = workExp.getPosition();

                WorkExperience workExperience = new WorkExperience(
                        spec,
                        workExp.getCompanyName(),
                        position,
                        workExp.getPeriod()
                );

                workExperienceRepository.save(workExperience);
            }
        }
    }

    private void saveCertifications(Spec spec, PostSpecRequest request) {
        if (isNotEmpty(request.getCertifications())) {
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
        if (isNotEmpty(request.getLanguageSkills())) {
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
        if (isNotEmpty(request.getActivities())) {
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

    private boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    private boolean isNotEmpty(Object obj) {
        return obj != null;
    }

}
