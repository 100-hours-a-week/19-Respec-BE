package kakaotech.bootcamp.respec.specranking.global.devsetup;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.education.entity.Education;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.education.repository.EducationRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.educationdetail.entity.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.educationdetail.repository.EducationDetailRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.workexperience.entity.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.workexperience.repository.WorkExperienceRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.global.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.global.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.type.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("spec-initialize")
@Order(2)
@RequiredArgsConstructor
public class InitializeSpec implements CommandLineRunner {
    private final UserRepository userRepository;
    private final SpecRepository specRepository;
    private final EducationRepository educationRepository;
    private final EducationDetailRepository educationDetailRepository;
    private final WorkExperienceRepository workExperienceRepository;

    private static final List<String> JOB_FIELDS = List.of(
            "인터넷_IT", "금융", "생산_제조", "영업_고객상담", "전문직_특수직",
            "연구개발_설계", "무역_유통", "건설", "미디어", "경영_사무"
    );
    private static final List<String> SCHOOL_NAMES = List.of(
            "서울대학교", "연세대학교", "고려대학교", "한양대학교", "성균관대학교",
            "서강대학교", "중앙대학교", "이화여자대학교", "경희대학교", "건국대학교"
    );
    private static final List<String> MAJORS = List.of(
            "컴퓨터공학", "소프트웨어공학", "전자공학", "기계공학", "산업공학",
            "경영학", "경제학", "법학", "의학", "약학",
            "간호학", "화학", "물리학", "생물학", "수학"
    );
    private static final List<String> COMPANIES = List.of(
            "네이버", "카카오", "라인", "쿠팡", "배달의민족",
            "당근마켓", "토스", "삼성전자", "LG전자", "현대자동차"
    );
    private static final List<Double> SCORES = List.of(
            65.5, 70.8, 75.2, 80.5, 85.7, 90.3, 95.8, 88.4, 77.9, 82.6,
            68.9, 73.4, 78.6, 83.1, 87.2, 92.5, 97.0, 86.3, 79.7, 84.8
    );
    private static final List<Double> GPAS = List.of(
            3.0, 3.2, 3.5, 3.7, 3.9, 3.1, 3.3, 3.6, 3.8, 4.0,
            3.15, 3.25, 3.45, 3.65, 3.85, 3.05, 3.35, 3.55, 3.75, 3.95
    );
    private static final List<Integer> PERIODS = List.of(
            6, 12, 18, 24, 36, 48, 60, 9, 15, 30,
            3, 8, 14, 20, 32, 42, 54, 7, 11, 22
    );
    private static final double MAX_GPA = 4.0;

    @Override
    @Transactional
    public void run(String... args) {
        List<User> users = userRepository.findAll();
        users.forEach(this::initializeForUser);
        log.info("{}개의 스펙 데이터가 성공적으로 생성되었습니다.", users.size());
    }

    private void initializeForUser(User user) {
        int idx = (int) (user.getId() % SCORES.size());
        Spec spec = buildSpec(user, idx);
        Spec savedSpec = specRepository.save(spec);
        saveEducation(savedSpec, idx);
        saveWorkExperience(savedSpec, idx);
    }

    private Spec buildSpec(User user, int idx) {
        String jobFieldStr = JOB_FIELDS.get(idx % JOB_FIELDS.size());
        JobField jobField = JobField.fromValue(jobFieldStr);

        double baseScore = SCORES.get(idx);
        double educationScore = Math.min(100, baseScore + 2);
        double workExperienceScore = Math.min(100, baseScore + 1);
        double activityNetworkingScore = Math.max(60, baseScore - 2);
        double certificationScore = Math.min(100, baseScore + 3);
        double englishSkillScore = baseScore;
        double totalScore = (educationScore + workExperienceScore + activityNetworkingScore
                + certificationScore + englishSkillScore) / 5.0;

        return new Spec(
                user,
                jobField,
                educationScore,
                workExperienceScore,
                activityNetworkingScore,
                certificationScore,
                englishSkillScore,
                totalScore,
                "AI 스펙 결과"
        );
    }

    private void saveEducation(Spec spec, int idx) {
        Education education = new Education(spec, Institute.UNIVERSITY, FinalStatus.GRADUATED);
        Education savedEdu = educationRepository.save(education);

        String schoolName = SCHOOL_NAMES.get(idx % SCHOOL_NAMES.size());
        Degree degree = Degree.BACHELOR;
        String major = MAJORS.get(idx % MAJORS.size());
        double gpa = GPAS.get(idx);

        EducationDetail detail = new EducationDetail(
                savedEdu,
                schoolName,
                degree,
                major,
                gpa,
                MAX_GPA
        );
        educationDetailRepository.save(detail);
    }

    private void saveWorkExperience(Spec spec, int idx) {
        String company = COMPANIES.get(idx % COMPANIES.size());
        Position position = Position.FULL_TIME_EMPLOYEE;
        int period = PERIODS.get(idx);

        WorkExperience workExp = new WorkExperience(
                spec,
                company,
                position,
                period
        );
        workExperienceRepository.save(workExp);
    }
}
