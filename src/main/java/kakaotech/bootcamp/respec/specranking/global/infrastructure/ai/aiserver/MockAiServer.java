package kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.aiserver;

import java.util.List;
import java.util.Random;
import kakaotech.bootcamp.respec.specranking.global.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.global.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.global.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.global.common.type.Position;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.ai.request.AiPostResumeRequest;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.ai.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.ai.response.AiPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.ai.response.AiPostResumeResponse.EducationDetail;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.ai.response.AiPostSpecResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!ai")
public class MockAiServer implements AiServer {
    private static final double MIN_SCORE = 20.0;
    private static final double MAX_SCORE = 100.0;

    @Override
    public AiPostSpecResponse analyzeSpec(AiPostSpecRequest aiPostSpecRequest) {
        return createMockAiResponse(aiPostSpecRequest);
    }

    @Override
    public AiPostResumeResponse analyzeResume(AiPostResumeRequest aiPostResumeRequest) {
        return createMockResumeResponse();
    }

    private AiPostSpecResponse createMockAiResponse(AiPostSpecRequest aiPostSpecRequest) {
        AiPostSpecResponse response = new AiPostSpecResponse();

        response.setNickname(aiPostSpecRequest.nickname());
        response.setEducationScore(generateRandomScore());
        response.setWorkExperienceScore(generateRandomScore());
        response.setCertificationScore(generateRandomScore());
        response.setLanguageSkillScore(generateRandomScore());
        response.setActivityNetworkingScore(generateRandomScore());

        Double avgScore = calculateAverageScoreWithBasicField(response);
        Double totalScore = adjustTotalScore(avgScore);
        response.setTotalScore(totalScore);
        response.setAssessment("스펙에 관한 AI 평가 내용이 들어갑니다.");
        return response;
    }


    private Double generateRandomScore() {
        Random random = new Random();
        return MIN_SCORE + (MAX_SCORE - MIN_SCORE) * random.nextDouble();
    }


    private Double calculateAverageScoreWithBasicField(AiPostSpecResponse response) {
        return (response.getEducationScore() +
                response.getWorkExperienceScore() +
                response.getCertificationScore() +
                response.getLanguageSkillScore() +
                response.getActivityNetworkingScore()) / 5.0;
    }


    private Double adjustTotalScore(Double avgScore) {
        Random random = new Random();
        Double adjustment = (random.nextDouble() * 10.0) - 5.0;
        Double adjusted = avgScore + adjustment;

        adjusted = guaranteeBetweenLowestAndHighest(adjusted);
        return adjusted;
    }

    private Double guaranteeBetweenLowestAndHighest(Double adjusted) {
        return Math.min(Math.max(adjusted, MIN_SCORE), MAX_SCORE);
    }

    private AiPostResumeResponse createMockResumeResponse() {
        List<EducationDetail> educationDetails = List.of(
                new AiPostResumeResponse.EducationDetail("카카오테크캠퍼스", Degree.BACHELOR.getValue(), "소프트웨어공학과", 3.8, 4.5),
                new AiPostResumeResponse.EducationDetail("서울대학교", Degree.MASTER.getValue(), "컴퓨터공학과", 4.2, 4.5)
        );

        List<AiPostResumeResponse.WorkExperience> workExperiences = List.of(
                new AiPostResumeResponse.WorkExperience("카카오", Position.CEO.getValue(), 24),
                new AiPostResumeResponse.WorkExperience("네이버", Position.INTERN.getValue(), 18)
        );

        List<String> certificates = List.of(
                "정보처리기사",
                "SQLD",
                "AWS Solutions Architect"
        );

        List<AiPostResumeResponse.LanguageSkill> languageSkills = List.of(
                new AiPostResumeResponse.LanguageSkill(LanguageTest.FLEX_CHINESE.getValue(), "850"),
                new AiPostResumeResponse.LanguageSkill(LanguageTest.FLEX_FRENCH.getValue(), "IH")
        );

        List<AiPostResumeResponse.Activity> activities = List.of(
                new AiPostResumeResponse.Activity("프로그래밍 동아리", "회장", "대상"),
                new AiPostResumeResponse.Activity("해커톤", "팀장", "금상"),
                new AiPostResumeResponse.Activity("오픈소스 기여", "개발자", "")
        );

        return new AiPostResumeResponse(
                Institute.UNIVERSITY.getValue(),
                FinalStatus.COMPLETED.getValue(),
                JobField.CONSTRUCTION.getValue(),
                educationDetails,
                workExperiences,
                certificates,
                languageSkills,
                activities
        );
    }
}
