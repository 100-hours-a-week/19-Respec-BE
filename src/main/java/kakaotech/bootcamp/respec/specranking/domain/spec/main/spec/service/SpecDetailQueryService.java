package kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.service;

import static kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus.ACTIVE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.dto.response.SpecDetailResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.dto.response.SpecDetailResponse.Details;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.dto.response.SpecDetailResponse.EducationDetails;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.dto.response.SpecDetailResponse.ScoreDetail;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.activitynetworking.entity.ActivityNetworking;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.activitynetworking.repository.ActivityNetworkingRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.certification.entity.Certification;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.certification.repository.CertificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.education.entity.Education;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.education.repository.EducationRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.educationdetail.entity.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.educationdetail.repository.EducationDetailRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.languageskill.entity.LanguageSkill;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.languageskill.repository.LanguageSkillRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.workexperience.entity.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.workexperience.repository.WorkExperienceRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.type.ScoreCategoryDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpecDetailQueryService {

    private final SpecRepository specRepository;
    private final EducationRepository educationRepository;
    private final EducationDetailRepository educationDetailRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final CertificationRepository certificationRepository;
    private final LanguageSkillRepository languageSkillRepository;
    private final ActivityNetworkingRepository activityNetworkingRepository;

    public SpecDetailResponse getSpecDetail(Long specId) {
        Spec spec = specRepository.findById(specId)
                .orElseThrow(() -> new IllegalArgumentException("Spec not found"));

        Optional<Long> userIdOpt = UserUtils.getCurrentUserId();
        if (!userIdOpt.isPresent()) {
            throw new IllegalArgumentException("로그인이 필요한 서비스입니다.");
        }

        Education education = educationRepository.findBySpecId(specId);
        List<WorkExperience> workExperiences = workExperienceRepository.findBySpecId(specId);
        List<Certification> certifications = certificationRepository.findBySpecId(specId);
        List<LanguageSkill> languages = languageSkillRepository.findBySpecId(specId);
        List<ActivityNetworking> activities = activityNetworkingRepository.findBySpecId(specId);
        JobField jobField = spec.getJobField();

        Long activeSpecCount = specRepository.countByStatus(ACTIVE);
        Long totalRank = specRepository.findAbsoluteRankByJobField(JobField.TOTAL, specId);
        Long jobFieldRank = specRepository.findAbsoluteRankByJobField(jobField, specId);
        Long jobFieldUserCount = specRepository.countByJobField(jobField);

        Details details = new Details(spec.getTotalAnalysisScore(), jobFieldRank, jobFieldUserCount, totalRank,
                activeSpecCount);

        List<ScoreDetail> categories = getScoreDetails(spec);
        SpecDetailResponse.Rankings rankings = new SpecDetailResponse.Rankings(details, categories);

        SpecDetailResponse.SpecDetailData response = new SpecDetailResponse.SpecDetailData(
                mappingEducation(education),
                mappingEducationDetails(education),
                mappingWorkExperience(workExperiences),
                mappingCertification(certifications),
                mappingLanguageSkill(languages),
                mappingActivities(activities),
                jobField, rankings, spec.getAssessment()
        );

        return new SpecDetailResponse(true, "세부 스펙 조회 성공!", response);

    }

    private List<ScoreDetail> getScoreDetails(Spec spec) {
        List<ScoreDetail> categories = new ArrayList<>();
        categories.add(createCategory(ScoreCategoryDetail.EDUCATION_SCORE, spec.getEducationScore()));
        categories.add(createCategory(ScoreCategoryDetail.WORK_EXPERIENCE, spec.getWorkExperienceScore()));
        categories.add(createCategory(ScoreCategoryDetail.CERTIFICATION_SKILLS, spec.getCertificationScore()));
        categories.add(createCategory(ScoreCategoryDetail.LANGUAGE_PROFICIENCY, spec.getEnglishSkillScore()));
        categories.add(createCategory(ScoreCategoryDetail.ACTIVITY_NETWORKING, spec.getActivityNetworkingScore()));
        return categories;
    }

    private static List<SpecDetailResponse.Activity> mappingActivities(List<ActivityNetworking> activities) {
        List<SpecDetailResponse.Activity> activityList = new ArrayList<>();

        for (ActivityNetworking act : activities) {
            SpecDetailResponse.Activity a = new SpecDetailResponse.Activity(act.getActivityName(), act.getPosition(),
                    act.getAward() != null && !act.getAward().isEmpty() ? act.getAward() : null);
            activityList.add(a);
        }
        return activityList;
    }

    private static List<SpecDetailResponse.LanguageSkill> mappingLanguageSkill(List<LanguageSkill> languages) {
        List<SpecDetailResponse.LanguageSkill> langList = new ArrayList<>();

        for (LanguageSkill lang : languages) {
            SpecDetailResponse.LanguageSkill l = new SpecDetailResponse.LanguageSkill(lang.getLanguageTest(),
                    lang.getScore());
            langList.add(l);
        }
        return langList;
    }

    private static List<SpecDetailResponse.Certification> mappingCertification(List<Certification> certifications) {
        List<SpecDetailResponse.Certification> certList = new ArrayList<>();

        for (Certification cert : certifications) {
            SpecDetailResponse.Certification c = new SpecDetailResponse.Certification(cert.getCertificationName());
            certList.add(c);
        }

        return certList;
    }

    private static List<SpecDetailResponse.WorkExperience> mappingWorkExperience(List<WorkExperience> workExperiences) {
        List<SpecDetailResponse.WorkExperience> workList = new ArrayList<>();

        for (WorkExperience we : workExperiences) {
            SpecDetailResponse.WorkExperience work = new SpecDetailResponse.WorkExperience(we.getCompanyName(),
                    we.getPosition(), we.getWorkMonth());
            workList.add(work);
        }

        return workList;
    }

    private List<EducationDetails> mappingEducationDetails(Education education) {
        if (education != null) {
            List<EducationDetail> educationDetails = educationDetailRepository.findByEducationId(education.getId());
            List<EducationDetails> educationDetailsList = new ArrayList<>();

            for (EducationDetail ed : educationDetails) {
                EducationDetails edu = new EducationDetails(ed.getSchoolName(), ed.getDegree(), ed.getMajor(),
                        ed.getGpa(), ed.getMaxGpa());
                educationDetailsList.add(edu);
            }
            return educationDetailsList;
        }
        return null;
    }

    private static SpecDetailResponse.FinalEducation mappingEducation(Education education) {
        if (education != null) {
            return new SpecDetailResponse.FinalEducation(
                    education.getInstitute(),
                    education.getStatus());
        }
        return null;
    }

    private ScoreDetail createCategory(ScoreCategoryDetail name, Double score) {
        return new ScoreDetail(name, score);
    }
}
