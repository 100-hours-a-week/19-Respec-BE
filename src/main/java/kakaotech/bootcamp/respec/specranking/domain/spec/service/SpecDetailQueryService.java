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
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
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

        SpecDetailResponse.SpecDetailData data = new SpecDetailResponse.SpecDetailData();

        Education education = educationRepository.findBySpecId(specId);
        if (education != null) {
            SpecDetailResponse.FinalEducation finalEducation = new SpecDetailResponse.FinalEducation();
            finalEducation.setInstitute(education.getInstitute());
            finalEducation.setFinalStatus(education.getStatus());
            data.setFinalEducation(finalEducation);
        }

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
            data.setEducationDetails(educationDetailsList);
        }

        List<WorkExperience> workExperiences = workExperienceRepository.findBySpecId(specId);
        List<SpecDetailResponse.WorkExperience> workList = new ArrayList<>();

        for (WorkExperience we : workExperiences) {
            SpecDetailResponse.WorkExperience work = new SpecDetailResponse.WorkExperience();
            work.setCompany(we.getCompanyName());
            work.setPosition(we.getPosition());
            work.setPeriod(we.getWorkMonth());
            workList.add(work);
        }
        data.setWorkExperiences(workList);

        List<Certification> certifications = certificationRepository.findBySpecId(specId);
        List<SpecDetailResponse.Certification> certList = new ArrayList<>();

        for (Certification cert : certifications) {
            SpecDetailResponse.Certification c = new SpecDetailResponse.Certification();
            c.setName(cert.getCertificationName());
            certList.add(c);
        }
        data.setCertifications(certList);

        List<LanguageSkill> languages = languageSkillRepository.findBySpecId(specId);
        List<SpecDetailResponse.LanguageSkill> langList = new ArrayList<>();

        for (LanguageSkill lang : languages) {
            SpecDetailResponse.LanguageSkill l = new SpecDetailResponse.LanguageSkill();
            l.setName(lang.getLanguageTest());
            l.setScore(lang.getScore());
            langList.add(l);
        }
        data.setLanguageSkills(langList);

        List<ActivityNetworking> activities = activityNetworkingRepository.findBySpecId(specId);
        List<SpecDetailResponse.Activity> activityList = new ArrayList<>();

        for (ActivityNetworking act : activities) {
            SpecDetailResponse.Activity a = new SpecDetailResponse.Activity();
            a.setName(act.getActivityName());
            a.setRole(act.getPosition());
            a.setAward(act.getAward() != null && !act.getAward().isEmpty() ? act.getAward() : null);
            activityList.add(a);
        }
        data.setActivities(activityList);

        data.setJobField(spec.getJobField());

        SpecDetailResponse.Rankings rankings = new SpecDetailResponse.Rankings();

        Details details = new Details();

        Long activeSpecCount = specRepository.countByStatus(ACTIVE);

        Long totalRank = specRepository.findAbsoluteRankByJobField(JobField.TOTAL, specId);

        JobField jobField = spec.getJobField();
        Long jobFieldRank = specRepository.findAbsoluteRankByJobField(jobField, specId);
        Long jobFieldUserCount = specRepository.countByJobField(jobField);

        details.setJobFieldRank(jobFieldRank);
        details.setJobFieldUserCount(jobFieldUserCount);

        details.setScore(spec.getTotalAnalysisScore());
        details.setTotalUserCount(activeSpecCount);
        details.setTotalRank(totalRank);

        rankings.setDetails(details);

        List<ScoreDetail> categories = new ArrayList<>();
        categories.add(createCategory(ScoreCategoryDetail.EDUCATION_SCORE, spec.getEducationScore()));
        categories.add(createCategory(ScoreCategoryDetail.WORK_EXPERIENCE, spec.getWorkExperienceScore()));
        categories.add(createCategory(ScoreCategoryDetail.CERTIFICATION_SKILLS, spec.getCertificationScore()));
        categories.add(createCategory(ScoreCategoryDetail.LANGUAGE_PROFICIENCY, spec.getEnglishSkillScore()));
        categories.add(createCategory(ScoreCategoryDetail.ACTIVITY_NETWORKING, spec.getActivityNetworkingScore()));
        rankings.setCategories(categories);

        data.setRankings(rankings);

        Optional<Portfolio> portfolio = portfolioRepository.findBySpecId(specId);
        if (portfolio.isPresent()) {
            data.setPortfolioUrl(portfolio.get().getOriginName());
        } else {
            data.setPortfolioUrl("");
        }

        return new SpecDetailResponse(true, "세부 스펙 조회 성공!", data);

    }

    private ScoreDetail createCategory(ScoreCategoryDetail name, Double score) {
        ScoreDetail scoreDetail = new ScoreDetail();
        scoreDetail.setName(name);
        scoreDetail.setScore(score);
        return scoreDetail;
    }
}
