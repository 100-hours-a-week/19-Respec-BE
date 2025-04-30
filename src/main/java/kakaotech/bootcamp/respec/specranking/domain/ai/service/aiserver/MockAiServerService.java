package kakaotech.bootcamp.respec.specranking.domain.ai.service.aiserver;

import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Profile("dev")
public class MockAiServerService implements AiServerService {

    private final Random random = new Random();

    private static final double MIN_SCORE = 25.0;
    private static final double MAX_SCORE = 95.0;

    @Override
    public AiPostSpecResponse call(AiPostSpecRequest aiPostSpecRequest) {
        AiPostSpecResponse response = new AiPostSpecResponse();

        response.setNickname(aiPostSpecRequest.getNickname());

        response.setAcademicScore(generateRandomScore());
        response.setWorkExperienceScore(generateRandomScore());
        response.setCertificationScore(generateRandomScore());
        response.setLanguageProficiencyScore(generateRandomScore());
        response.setExtracurricularScore(generateRandomScore());

        double avgScore = calculateAverageScore(response);
        double totalScore = adjustTotalScore(avgScore);
        response.setTotalScore(totalScore);

        return response;
    }


    private double generateRandomScore() {
        double rawScore = MIN_SCORE + (MAX_SCORE - MIN_SCORE) * random.nextDouble();
        return Math.round(rawScore * 10.0) / 10.0;
    }


    private double calculateAverageScore(AiPostSpecResponse response) {
        return (response.getAcademicScore() +
                response.getWorkExperienceScore() +
                response.getCertificationScore() +
                response.getLanguageProficiencyScore() +
                response.getExtracurricularScore()) / 5.0;
    }


    private double adjustTotalScore(double avgScore) {
        double adjustment = (random.nextDouble() * 10.0) - 5.0; // -5.0 ~ +5.0 사이의 랜덤 값
        double adjusted = avgScore + adjustment;

        adjusted = Math.min(Math.max(adjusted, MIN_SCORE), MAX_SCORE);
        return Math.round(adjusted * 10.0) / 10.0;
    }
}