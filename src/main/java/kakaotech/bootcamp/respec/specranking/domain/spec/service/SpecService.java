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
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Portfolio;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.ActivityNetworkingRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.CertificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.EducationDetailRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.EducationRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.EnglishSkillRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.PortfolioRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.WorkExperienceRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.util.MockGetCurrentUser;
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
    private final EnglishSkillRepository englishSkillRepository;
    private final ActivityNetworkingRepository activityNetworkingRepository;
    private final PortfolioRepository portfolioRepository;

    public void createSpec(PostSpecRequest request) {
        Long userId = MockGetCurrentUser.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. ID: " + userId));

        AiPostSpecRequest aiPostSpecRequest = aiService.convertToAiRequest(request);
        AiPostSpecResponse aiPostSpecResponse = aiService.analyzeSpec(aiPostSpecRequest);

        Spec spec = new Spec(
                user,
                request.getJobField(),
                aiPostSpecResponse.getTotalScore()
        );

        Spec savedSpec = specRepository.save(spec);

        saveEducation(savedSpec, request, aiPostSpecResponse.getAcademicScore());
        saveWorkExperience(savedSpec, request, aiPostSpecResponse.getWorkExperienceScore());
        saveCertifications(savedSpec, request, aiPostSpecResponse.getCertificationScore());
        saveLanguageSkills(savedSpec, request, aiPostSpecResponse.getLanguageProficiencyScore());
        saveActivities(savedSpec, request, aiPostSpecResponse.getExtracurricularScore());
    }

    private void saveEducation(Spec spec, PostSpecRequest request, double academicScore) {
        if (request.getFinalEducation() != null) {
            EducationInstitute institute = convertToEducationInstitute(request.getFinalEducation().getStatus());
            EducationStatus status = convertToEducationStatus(request.getFinalEducation().getLevel());

            Education education = new Education(spec, institute, status, academicScore);
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

    private void saveWorkExperience(Spec spec, PostSpecRequest request, double workScore) {
        if (request.getWorkExperience() != null && !request.getWorkExperience().isEmpty()) {

            double individualScore = workScore / request.getWorkExperience().size();

            for (PostSpecRequest.WorkExperience workExp : request.getWorkExperience()) {
                WorkPosition position = convertToWorkPosition(workExp.getPosition());

                WorkExperience workExperience = new WorkExperience(
                        spec,
                        workExp.getCompany(),
                        position,
                        workExp.getPeriod(),
                        individualScore
                );

                workExperienceRepository.save(workExperience);
            }
        }
    }

    private void saveCertifications(Spec spec, PostSpecRequest request, double certScore) {
        if (request.getCertifications() != null && !request.getCertifications().isEmpty()) {
            double individualScore = certScore / request.getCertifications().size();

            for (PostSpecRequest.Certification certificationDto : request.getCertifications()) {
                Certification certification = new Certification(
                        spec,
                        certificationDto.getName(),
                        individualScore
                );

                certificationRepository.save(certification);
            }
        }
    }

    private void saveLanguageSkills(Spec spec, PostSpecRequest request, double langScore) {
        if (request.getLanguageSkills() != null && !request.getLanguageSkills().isEmpty()) {
            // 전체 언어 능력 항목 수로 점수 분배
            double individualScore = langScore / request.getLanguageSkills().size();

            for (PostSpecRequest.LanguageSkill languageSkillDto : request.getLanguageSkills()) {
                EnglishSkill englishSkill = new EnglishSkill(
                        spec,
                        languageSkillDto.getName(),
                        "English", // 기본값 설정
                        languageSkillDto.getScore(),
                        individualScore
                );

                englishSkillRepository.save(englishSkill);
            }
        }
    }

    private void saveActivities(Spec spec, PostSpecRequest request, double activityScore) {
        if (request.getActivities() != null && !request.getActivities().isEmpty()) {
            // 전체 활동 항목 수로 점수 분배
            double individualScore = activityScore / request.getActivities().size();

            for (PostSpecRequest.Activity activityDto : request.getActivities()) {
                ActivityNetworking activity = new ActivityNetworking(
                        spec,
                        activityDto.getName(),
                        activityDto.getRole(),
                        activityDto.getAward(),
                        individualScore
                );

                activityNetworkingRepository.save(activity);
            }
        }
    }

    private void savePortfolio(Spec spec, String fileUrl, String originName) {
        if (fileUrl != null && !fileUrl.isEmpty()) {
            Portfolio portfolio = new Portfolio(
                    spec,
                    fileUrl,
                    originName
            );

            portfolioRepository.save(portfolio);
        }
    }

    private EducationInstitute convertToEducationInstitute(String status) {
        if (status == null) return EducationInstitute.ENROLLED;

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
        if (level == null) return EducationStatus.HIGH_SCHOOL;

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
        if (degree == null) return DegreeType.BACHELOR;

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
        if (position == null) return WorkPosition.INTERN;

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
