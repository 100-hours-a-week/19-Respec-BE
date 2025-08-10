package kakaotech.bootcamp.respec.specranking.domain.spec.spec.service;

import static kakaotech.bootcamp.respec.specranking.domain.auth.constant.AuthConstant.LOGIN_REQUIRED_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.constant.SpecConstant.SPEC_DUPLICATE_TRY_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.constant.SpecConstant.SPEC_NOT_ABLE_UPDATE_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.constant.SpecConstant.SPEC_UPDATE_FORBIDDEN_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.user.constants.UserConstant.USER_NOT_FOUND_MESSAGE;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.auth.exception.LoginRequiredException;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.mapping.AiDtoMapping;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.request.PostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.request.PostSpecRequest.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.exception.SpecDuplicateTryException;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.exception.SpecNotAbleUpdateException;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.exception.SpecUpdateForbiddenException;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.activitynetworking.entity.ActivityNetworking;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.activitynetworking.repository.ActivityNetworkingRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.certification.entity.Certification;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.certification.repository.CertificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.education.entity.Education;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.education.repository.EducationRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.educationdetail.repository.EducationDetailRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.languageskill.entity.LanguageSkill;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.languageskill.repository.LanguageSkillRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.workexperience.entity.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.workexperience.repository.WorkExperienceRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.exception.UserNotFoundException;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.global.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.global.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.global.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.global.common.type.Position;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.response.AiPostSpecResponse;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.service.AiService;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SpecService {

    private static final Duration IDEMPOTENCY_TTL = Duration.ofMinutes(3);

    private final AiService aiService;
    private final IdempotencyService idempotencyService;
    private final UserRepository userRepository;
    private final SpecRepository specRepository;
    private final EducationRepository educationRepository;
    private final EducationDetailRepository educationDetailRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final CertificationRepository certificationRepository;
    private final LanguageSkillRepository languageSkillRepository;
    private final ActivityNetworkingRepository activityNetworkingRepository;

    public void createSpec(PostSpecRequest request) {
        final String idempotentKey = request.idempotentKey();

        try {
            if (!idempotencyService.setIfAbsent(idempotentKey, IDEMPOTENCY_TTL)) {
                return;
            }

            Optional<Long> userIdOpt = UserUtils.getCurrentUserId();
            Long userId = userIdOpt.orElseThrow(() -> new LoginRequiredException(LOGIN_REQUIRED_MESSAGE));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + " ID: " + userId));

            validateMultipleSpec(userId);

            AiPostSpecRequest aiPostSpecRequest = AiDtoMapping.convertToSpecAnalysisRequest(request,
                    user.getNickname());
            AiPostSpecResponse aiPostSpecResponse = aiService.analyzeSpec(aiPostSpecRequest);

            saveSpecWithChaining(request, aiPostSpecResponse, user);

        } catch (Exception e) {
            if (idempotencyService.hasKey(idempotentKey)) {
                idempotencyService.delete(idempotentKey);
            }
            throw e;
        }
    }

    public void updateSpec(Long specId, PostSpecRequest request) {
        final String idempotentKey = request.idempotentKey();

        try {
            if (!idempotencyService.setIfAbsent(idempotentKey, IDEMPOTENCY_TTL)) {
                return;
            }

            Optional<Long> userIdOpt = UserUtils.getCurrentUserId();
            Long userId = userIdOpt.orElseThrow(() -> new LoginRequiredException(LOGIN_REQUIRED_MESSAGE));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + " ID: " + userId));

            Spec spec = specRepository.findByIdAndStatus(specId, SpecStatus.ACTIVE)
                    .orElseThrow(() -> new SpecNotAbleUpdateException(SPEC_NOT_ABLE_UPDATE_MESSAGE + " ID: " + specId));

            if (!spec.getUser().equals(user)) {
                throw new SpecUpdateForbiddenException(SPEC_UPDATE_FORBIDDEN_MESSAGE);
            }

            AiPostSpecRequest aiPostSpecRequest = AiDtoMapping.convertToSpecAnalysisRequest(request,
                    user.getNickname());
            AiPostSpecResponse aiPostSpecResponse = aiService.analyzeSpec(aiPostSpecRequest);

            spec.delete();
            saveSpecWithChaining(request, aiPostSpecResponse, user);

        } catch (Exception e) {
            if (idempotencyService.hasKey(idempotentKey)) {
                idempotencyService.delete(idempotentKey);
            }
            throw e;
        }
    }

    private void validateMultipleSpec(Long userId) {
        Optional<Spec> existingActiveSpec = specRepository.findByUserIdAndStatus(userId, SpecStatus.ACTIVE);
        if (existingActiveSpec.isPresent()) {
            throw new SpecDuplicateTryException(SPEC_DUPLICATE_TRY_MESSAGE);
        }
    }

    private void saveSpecWithChaining(PostSpecRequest request, AiPostSpecResponse aiPostSpecResponse, User user) {
        Spec newSpec = Spec.createFromAiResponse(user, request.jobField(), aiPostSpecResponse);
        Spec savedNewSpec = specRepository.save(newSpec);

        saveEducation(savedNewSpec, request);
        saveWorkExperience(savedNewSpec, request);
        saveCertifications(savedNewSpec, request);
        saveLanguageSkills(savedNewSpec, request);
        saveActivities(savedNewSpec, request);
    }

    private void saveEducation(Spec spec, PostSpecRequest request) {
        if (isNotEmpty(request.finalEducation())) {
            FinalStatus status = request.finalEducation().status();
            Institute institute = request.finalEducation().institute();

            Education education = new Education(spec, institute, status);
            Education savedEducation = educationRepository.save(education);

            if (isNotEmpty(request.educationDetails())) {
                for (EducationDetail educationDetailDto : request.educationDetails()) {
                    Degree degree = educationDetailDto.degree();

                    kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.educationdetail.entity.EducationDetail educationDetail = new kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.educationdetail.entity.EducationDetail(
                            savedEducation,
                            educationDetailDto.schoolName(),
                            degree,
                            educationDetailDto.major(),
                            educationDetailDto.gpa(),
                            educationDetailDto.maxGpa()
                    );

                    educationDetailRepository.save(educationDetail);
                }
            }
        }
    }

    private void saveWorkExperience(Spec spec, PostSpecRequest request) {
        if (isNotEmpty(request.workExperiences())) {
            for (PostSpecRequest.WorkExperience workExp : request.workExperiences()) {
                Position position = workExp.position();

                WorkExperience workExperience = new WorkExperience(
                        spec, workExp.companyName(),
                        position, workExp.period()
                );

                workExperienceRepository.save(workExperience);
            }
        }
    }

    private void saveCertifications(Spec spec, PostSpecRequest request) {
        if (isNotEmpty(request.certifications())) {
            for (PostSpecRequest.Certification certificationDto : request.certifications()) {
                Certification certification = new Certification(
                        spec, certificationDto.name()
                );
                certificationRepository.save(certification);
            }
        }
    }

    private void saveLanguageSkills(Spec spec, PostSpecRequest request) {
        if (isNotEmpty(request.languageSkills())) {
            for (PostSpecRequest.LanguageSkill languageSkillDto : request.languageSkills()) {
                LanguageSkill languageSkill = new LanguageSkill(
                        spec,
                        languageSkillDto.languageTest(),
                        languageSkillDto.score()
                );
                languageSkillRepository.save(languageSkill);
            }
        }
    }

    private void saveActivities(Spec spec, PostSpecRequest request) {
        if (isNotEmpty(request.activities())) {
            for (PostSpecRequest.Activity activityDto : request.activities()) {
                ActivityNetworking activity = new ActivityNetworking(
                        spec,
                        activityDto.name(),
                        activityDto.role(),
                        activityDto.award()
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
