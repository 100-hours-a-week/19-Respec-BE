package kakaotech.bootcamp.respec.specranking.domain.spec.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.activitynetworking.entity.ActivityNetworking;
import kakaotech.bootcamp.respec.specranking.domain.activitynetworking.repository.ActivityNetworkingRepository;
import kakaotech.bootcamp.respec.specranking.domain.certification.entity.Certification;
import kakaotech.bootcamp.respec.specranking.domain.certification.repository.CertificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecCategory;
import kakaotech.bootcamp.respec.specranking.domain.education.entity.Education;
import kakaotech.bootcamp.respec.specranking.domain.education.repository.EducationRepository;
import kakaotech.bootcamp.respec.specranking.domain.educationdetail.entity.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.educationdetail.repository.EducationDetailRepository;
import kakaotech.bootcamp.respec.specranking.domain.languageskill.entity.LanguageSkill;
import kakaotech.bootcamp.respec.specranking.domain.languageskill.repository.LanguageSkillRepository;
import kakaotech.bootcamp.respec.specranking.domain.portfolio.entity.Portfolio;
import kakaotech.bootcamp.respec.specranking.domain.portfolio.repository.PortfolioRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SpecDetailResponse;
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
        data.setSpecId(spec.getId());

        Education education = educationRepository.findBySpecId(specId);
        if (education != null) {
            SpecDetailResponse.FinalEducation finalEducation = new SpecDetailResponse.FinalEducation();
            finalEducation.setLevel(education.getInstitute().getValue());
            finalEducation.setStatus(education.getStatus().getValue());
            data.setFinalEducation(finalEducation);
        }

        if (education != null) {
            List<EducationDetail> educationDetails = educationDetailRepository.findByEducationId(education.getId());
            List<SpecDetailResponse.Education> educationList = new ArrayList<>();

            for (EducationDetail ed : educationDetails) {
                SpecDetailResponse.Education edu = new SpecDetailResponse.Education();
                edu.setSchoolName(ed.getSchoolName());
                edu.setDegree(ed.getDegree().getValue());
                edu.setMajor(ed.getMajor());
                edu.setGpa(ed.getGpa());
                edu.setMaxGpa(ed.getMaxGpa());
                educationList.add(edu);
            }
            data.setEducations(educationList);
        }

        List<WorkExperience> workExperiences = workExperienceRepository.findBySpecId(specId);
        List<SpecDetailResponse.WorkExperience> workList = new ArrayList<>();

        for (WorkExperience we : workExperiences) {
            SpecDetailResponse.WorkExperience work = new SpecDetailResponse.WorkExperience();
            work.setCompany(we.getCompanyName());
            work.setPosition(we.getPosition().getValue());
            work.setPeriod(we.getWorkMonth());
            workList.add(work);
        }
        data.setWorkExperience(workList);

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
            l.setName(lang.getLanguageTest().getValue());
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

        data.setJobField(spec.getWorkPosition().getValue());

        SpecDetailResponse.Rankings rankings = new SpecDetailResponse.Rankings();

        SpecDetailResponse.Overall overall = new SpecDetailResponse.Overall();
        overall.setScore(spec.getTotalAnalysisScore());

        Map<String, Integer> jobFieldUserCountMap = specRepository.countByJobFields();
        int totalUserCount = jobFieldUserCountMap.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        overall.setTotalUserCount(totalUserCount);

        long rank = specRepository.findAbsoluteRank(JobField.TOTAL, specId);
        overall.setRank((int) rank);

        rankings.setOverall(overall);

        List<SpecDetailResponse.Category> categories = new ArrayList<>();
        categories.add(createCategory(SpecCategory.EDUCATION_GPA, spec.getEducationScore()));
        categories.add(createCategory(SpecCategory.WORK_EXPERIENCE, spec.getWorkExperienceScore()));
        categories.add(createCategory(SpecCategory.CERTIFICATION_SKILLS, spec.getCertificationScore()));
        categories.add(createCategory(SpecCategory.LANGUAGE_PROFICIENCY, spec.getEnglishSkillScore()));
        categories.add(createCategory(SpecCategory.ACTIVITY_NETWORKING, spec.getActivityNetworkingScore()));
        rankings.setCategories(categories);

        data.setRankings(rankings);

        Optional<Portfolio> portfolio = portfolioRepository.findBySpecId(specId);
        if (portfolio.isPresent()) {
            data.setPortfolioUrl(portfolio.get().getOriginName());
        } else {
            data.setPortfolioUrl("");
        }

        return SpecDetailResponse.success(data);
    }

    private SpecDetailResponse.Category createCategory(SpecCategory name, Double score) {
        SpecDetailResponse.Category category = new SpecDetailResponse.Category();
        category.setName(name);
        category.setScore(score);
        return category;
    }
}
