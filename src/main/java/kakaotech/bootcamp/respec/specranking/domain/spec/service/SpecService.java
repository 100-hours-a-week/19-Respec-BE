package kakaotech.bootcamp.respec.specranking.domain.spec.service;

import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;
import kakaotech.bootcamp.respec.specranking.domain.ai.service.AiService;
import kakaotech.bootcamp.respec.specranking.domain.common.type.DegreeType;
import kakaotech.bootcamp.respec.specranking.domain.common.type.EducationInstitute;
import kakaotech.bootcamp.respec.specranking.domain.common.type.EducationStatus;
import kakaotech.bootcamp.respec.specranking.domain.common.type.WorkPosition;
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
            EducationInstitute institute = convertToEducationInstitute(request.getFinalEducation().getStatus());
            EducationStatus status = convertToEducationStatus(request.getFinalEducation().getLevel());

            Education education = new Education(spec, institute, status);
            Education savedEducation = educationRepository.save(education);

            if (request.getEducations() != null && !request.getEducations().isEmpty()) {
                for (PostSpecRequest.Education educationDto : request.getEducations()) {
                    DegreeType degreeType = convertToDegreeType(educationDto.getDegree());

                    EducationDetail educationDetail = new EducationDetail(
                            savedEducation,
                            educationDto.getSchoolName(),
                            degreeType,
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
                WorkPosition position = convertToWorkPosition(workExp.getPosition());

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

    private EducationInstitute convertToEducationInstitute(String status) {
        if (status == null) {
            return EducationInstitute.ENROLLED;
        }

        switch (status.toUpperCase()) {
            case "졸업":
                return EducationInstitute.GRADUATED;
            case "수료":
                return EducationInstitute.COMPLETED;
            case "중퇴":
                return EducationInstitute.DROPPED_OUT;
            case "휴학":
                return EducationInstitute.WITHDRAWN;
            default:
                return EducationInstitute.ENROLLED;
        }
    }

    private EducationStatus convertToEducationStatus(String level) {
        if (level == null) {
            return EducationStatus.HIGH_SCHOOL;
        }

        switch (level.toUpperCase()) {
            case "중학교":
                return EducationStatus.MIDDLE_SCHOOL;
            case "고등학교":
                return EducationStatus.HIGH_SCHOOL;
            case "전문대":
                return EducationStatus.TWO_THREE_YEAR_COLLEGE;
            case "대학교":
                return EducationStatus.UNIVERSITY;
            case "대학원":
                return EducationStatus.GRADUATE_SCHOOL;
            default:
                return EducationStatus.HIGH_SCHOOL;
        }
    }

    private DegreeType convertToDegreeType(String degree) {
        if (degree == null) {
            return DegreeType.BACHELOR;
        }

        switch (degree.toUpperCase()) {
            case "박사":
                return DegreeType.DOCTORATE;
            case "석사":
                return DegreeType.MASTER;
            case "학사":
                return DegreeType.BACHELOR;
            case "전문학사":
                return DegreeType.ASSOCIATE;
            default:
                return DegreeType.CERTIFICATE;
        }
    }

    private WorkPosition convertToWorkPosition(String position) {
        if (position == null) {
            return WorkPosition.INTERN;
        }

        switch (position.toUpperCase()) {
            case "CEO":
                return WorkPosition.CEO;
            case "정규직":
                return WorkPosition.FULL_TIME_EMPLOYEE;
            default:
                return WorkPosition.INTERN;
        }
    }
}
