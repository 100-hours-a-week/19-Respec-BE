package kakaotech.bootcamp.respec.specranking.domain.spec.util;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.CareerRole;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalEducation;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.domain.education.entity.Education;
import kakaotech.bootcamp.respec.specranking.domain.education.repository.EducationRepository;
import kakaotech.bootcamp.respec.specranking.domain.educationdetail.entity.EducationDetail;
import kakaotech.bootcamp.respec.specranking.domain.educationdetail.repository.EducationDetailRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.workexperience.entity.WorkExperience;
import kakaotech.bootcamp.respec.specranking.domain.workexperience.repository.WorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
@Order(2)
@RequiredArgsConstructor
public class InitializeSpec implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SpecRepository specRepository;
    private final EducationRepository educationRepository;
    private final EducationDetailRepository educationDetailRepository;
    private final WorkExperienceRepository workExperienceRepository;

    private final String[] jobFields = {
            "인터넷.IT", "금융", "제조", "서비스", "의료", "교육", "물류", "건설", "미디어", "법률"
    };

    private final String[] schoolNames = {
            "서울대학교", "연세대학교", "고려대학교", "한양대학교", "성균관대학교",
            "서강대학교", "중앙대학교", "이화여자대학교", "경희대학교", "건국대학교"
    };

    private final String[] majors = {
            "컴퓨터공학", "소프트웨어공학", "전자공학", "기계공학", "산업공학",
            "경영학", "경제학", "법학", "의학", "약학", "간호학", "화학", "물리학", "생물학", "수학"
    };

    private final String[] companies = {
            "네이버", "카카오", "라인", "쿠팡", "배달의민족", "당근마켓", "토스", "삼성전자", "LG전자", "현대자동차"
    };

    private final double[] scores = {
            65.5, 70.8, 75.2, 80.5, 85.7, 90.3, 95.8, 88.4, 77.9, 82.6,
            68.9, 73.4, 78.6, 83.1, 87.2, 92.5, 97.0, 86.3, 79.7, 84.8
    };

    private final double[] gpas = {
            3.0, 3.2, 3.5, 3.7, 3.9, 3.1, 3.3, 3.6, 3.8, 4.0,
            3.15, 3.25, 3.45, 3.65, 3.85, 3.05, 3.35, 3.55, 3.75, 3.95
    };

    private final int[] periods = {
            6, 12, 18, 24, 36, 48, 60, 9, 15, 30,
            3, 8, 14, 20, 32, 42, 54, 7, 11, 22
    };

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<User> users = userRepository.findAll();

        System.out.println("스펙 데이터 초기화 시작: " + users.size() + "명의 사용자에 대한 스펙 생성");

        int count = 0;
        for (User user : users) {
            int index = (int) (user.getId() % 20);
            createSpecForUser(user, index);
            count++;
        }

        System.out.println(count + "개의 스펙 데이터가 성공적으로 생성되었습니다.");
    }

    private void createSpecForUser(User user, int index) {
        String jobField = jobFields[index % jobFields.length];
        double score = scores[index % scores.length];
        double educationScore = Math.min(100, score + 2);
        double workExperienceScore = Math.min(100, score + 1);
        double activityNetworkingScore = Math.max(60, score - 2);
        double certificationScore = Math.min(100, score + 3);
        double englishSkillScore = score;
        double totalScore = (educationScore + workExperienceScore + activityNetworkingScore
                + certificationScore + englishSkillScore) / 5.0;

        Spec spec = new Spec(
                user,
                jobField,
                educationScore,
                workExperienceScore,
                activityNetworkingScore,
                certificationScore,
                englishSkillScore,
                totalScore
        );

        Spec savedSpec = specRepository.save(spec);

        FinalStatus finalStatus = FinalStatus.GRADUATED;
        FinalEducation finalEducation = FinalEducation.UNIVERSITY;

        Education education = new Education(
                savedSpec,
                finalStatus,
                finalEducation
        );

        Education savedEducation = educationRepository.save(education);

        String schoolName = schoolNames[index % schoolNames.length];
        Degree degree = Degree.BACHELOR;
        String major = majors[index % majors.length];
        double gpa = gpas[index % gpas.length];
        double maxGpa = 4.0;

        EducationDetail educationDetail = new EducationDetail(
                savedEducation,
                schoolName,
                degree,
                major,
                gpa,
                maxGpa
        );

        educationDetailRepository.save(educationDetail);

        String company = companies[index % companies.length];
        CareerRole careerRole = CareerRole.FULL_TIME_EMPLOYEE;
        int period = periods[index % periods.length];

        WorkExperience workExperience = new WorkExperience(
                savedSpec,
                company,
                careerRole,
                period
        );

        workExperienceRepository.save(workExperience);
    }
}
