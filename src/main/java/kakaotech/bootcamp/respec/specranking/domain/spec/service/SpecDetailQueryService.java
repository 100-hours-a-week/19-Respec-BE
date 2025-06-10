package kakaotech.bootcamp.respec.specranking.domain.spec.service;

import static kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus.ACTIVE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.activitynetworking.entity.ActivityNetworking;
import kakaotech.bootcamp.respec.specranking.domain.activitynetworking.repository.ActivityNetworkingRepository;
import kakaotech.bootcamp.respec.specranking.domain.certification.entity.Certification;
import kakaotech.bootcamp.respec.specranking.domain.certification.repository.CertificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.common.type.ScoreCategoryDetail;
import kakaotech.bootcamp.respec.specranking.domain.education.entity.Education;
import kakaotech.bootcamp.respec.specranking.domain.education.repository.EducationRepository;
import kakaotech.bootcamp.respec.specranking.domain.educationdetail.entity.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.educationdetail.repository.EducationDetailRepository;
import kakaotech.bootcamp.respec.specranking.domain.languageskill.entity.LanguageSkill;
import kakaotech.bootcamp.respec.specranking.domain.languageskill.repository.LanguageSkillRepository;
import kakaotech.bootcamp.respec.specranking.domain.portfolio.entity.Portfolio;
import kakaotech.bootcamp.respec.specranking.domain.portfolio.repository.PortfolioRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SpecDetailResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SpecDetailResponse.Details;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SpecDetailResponse.EducationDetails;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SpecDetailResponse.ScoreDetail;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SpecDetailResponse.SpecDetailData;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.domain.workexperience.entity.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.workexperience.repository.WorkExperienceRepository;
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
    private final PortfolioRepository portfolioRepository;

    public SpecDetailResponse getSpecDetail(Long specId) {
        Spec spec = specRepository.findById(specId)
                .orElseThrow(() -> new IllegalArgumentException("Spec not found"));

        Optional<Long> userIdOpt = UserUtils.getCurrentUserId();
        if (!userIdOpt.isPresent()) {
            throw new IllegalArgumentException("로그인이 필요한 서비스입니다.");
        }

        SpecDetailResponse.SpecDetailData response = new SpecDetailResponse.SpecDetailData();

        Education education = educationRepository.findBySpecId(specId);
        List<WorkExperience> workExperiences = workExperienceRepository.findBySpecId(specId);
        List<Certification> certifications = certificationRepository.findBySpecId(specId);
        List<LanguageSkill> languages = languageSkillRepository.findBySpecId(specId);
        List<ActivityNetworking> activities = activityNetworkingRepository.findBySpecId(specId);
        JobField jobField = spec.getJobField();

        mappingEducation(education, response);
        mappingEducationDetails(education, response);
        mappingWorkExperience(workExperiences, response);
        mappingCertification(certifications, response);
        mappingLanguageSkill(languages, response);
        mappingActivities(activities, response);
        response.setJobField(jobField);

        SpecDetailResponse.Rankings rankings = new SpecDetailResponse.Rankings();

        Long activeSpecCount = specRepository.countByStatus(ACTIVE);
        Long totalRank = specRepository.findAbsoluteRankByJobField(JobField.TOTAL, specId);
        Long jobFieldRank = specRepository.findAbsoluteRankByJobField(jobField, specId);
        Long jobFieldUserCount = specRepository.countByJobField(jobField);

        Details details = new Details();
        details.setJobFieldRank(jobFieldRank);
        details.setJobFieldUserCount(jobFieldUserCount);
        details.setScore(spec.getTotalAnalysisScore());
        details.setTotalUserCount(activeSpecCount);
        details.setTotalRank(totalRank);
        rankings.setDetails(details);

        List<ScoreDetail> categories = getScoreDteails(spec);
        rankings.setCategories(categories);
        response.setRankings(rankings);

        Optional<Portfolio> portfolio = portfolioRepository.findBySpecId(specId);
        if (portfolio.isPresent()) {
            response.setPortfolioUrl(portfolio.get().getOriginName());
        } else {
            response.setPortfolioUrl("");
        }

        response.setAssessment(spec.getAssessment());

        return new SpecDetailResponse(true, "세부 스펙 조회 성공!", response);

    }

    private List<ScoreDetail> getScoreDteails(Spec spec) {
        List<ScoreDetail> categories = new ArrayList<>();
        categories.add(createCategory(ScoreCategoryDetail.EDUCATION_SCORE, spec.getEducationScore()));
        categories.add(createCategory(ScoreCategoryDetail.WORK_EXPERIENCE, spec.getWorkExperienceScore()));
        categories.add(createCategory(ScoreCategoryDetail.CERTIFICATION_SKILLS, spec.getCertificationScore()));
        categories.add(createCategory(ScoreCategoryDetail.LANGUAGE_PROFICIENCY, spec.getEnglishSkillScore()));
        categories.add(createCategory(ScoreCategoryDetail.ACTIVITY_NETWORKING, spec.getActivityNetworkingScore()));
        return categories;
    }

    private static void mappingActivities(List<ActivityNetworking> activities, SpecDetailData response) {
        List<SpecDetailResponse.Activity> activityList = new ArrayList<>();

        for (ActivityNetworking act : activities) {
            SpecDetailResponse.Activity a = new SpecDetailResponse.Activity();
            a.setName(act.getActivityName());
            a.setRole(act.getPosition());
            a.setAward(act.getAward() != null && !act.getAward().isEmpty() ? act.getAward() : null);
            activityList.add(a);
        }
        response.setActivities(activityList);
    }

    private static void mappingLanguageSkill(List<LanguageSkill> languages, SpecDetailData response) {
        List<SpecDetailResponse.LanguageSkill> langList = new ArrayList<>();

        for (LanguageSkill lang : languages) {
            SpecDetailResponse.LanguageSkill l = new SpecDetailResponse.LanguageSkill();
            l.setName(lang.getLanguageTest());
            l.setScore(lang.getScore());
            langList.add(l);
        }
        response.setLanguageSkills(langList);
    }

    private static void mappingCertification(List<Certification> certifications, SpecDetailData response) {
        List<SpecDetailResponse.Certification> certList = new ArrayList<>();

        for (Certification cert : certifications) {
            SpecDetailResponse.Certification c = new SpecDetailResponse.Certification();
            c.setName(cert.getCertificationName());
            certList.add(c);
        }
        response.setCertifications(certList);
    }

    private static void mappingWorkExperience(List<WorkExperience> workExperiences, SpecDetailData response) {
        List<SpecDetailResponse.WorkExperience> workList = new ArrayList<>();

        for (WorkExperience we : workExperiences) {
            SpecDetailResponse.WorkExperience work = new SpecDetailResponse.WorkExperience();
            work.setCompany(we.getCompanyName());
            work.setPosition(we.getPosition());
            work.setPeriod(we.getWorkMonth());
            workList.add(work);
        }
        response.setWorkExperiences(workList);
    }

    private void mappingEducationDetails(Education education, SpecDetailData response) {
        if (education != null) {
            List<EducationDetail> educationDetails = educationDetailRepository.findByEducationId(education.getId());
            List<EducationDetails> educationDetailsList = new ArrayList<>();

            for (EducationDetail ed : educationDetails) {
                EducationDetails edu = new EducationDetails();
                edu.setSchoolName(ed.getSchoolName());
                edu.setDegree(ed.getDegree());
                edu.setMajor(ed.getMajor());
                edu.setGpa(ed.getGpa());
                edu.setMaxGpa(ed.getMaxGpa());
                educationDetailsList.add(edu);
            }
            response.setEducationDetails(educationDetailsList);
        }
    }

    private static void mappingEducation(Education education, SpecDetailData data) {
        if (education != null) {
            SpecDetailResponse.FinalEducation finalEducation = new SpecDetailResponse.FinalEducation();
            finalEducation.setInstitute(education.getInstitute());
            finalEducation.setFinalStatus(education.getStatus());
            data.setFinalEducation(finalEducation);
        }
    }

    private ScoreDetail createCategory(ScoreCategoryDetail name, Double score) {
        ScoreDetail scoreDetail = new ScoreDetail();
        scoreDetail.setName(name);
        scoreDetail.setScore(score);
        return scoreDetail;
    }
}
