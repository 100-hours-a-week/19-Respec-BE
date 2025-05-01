package kakaotech.bootcamp.respec.specranking.domain.ai.aiserver;

import java.util.Random;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class MockAiServer implements AiServer {

    private final Random random = new Random();

    private static final double MIN_SCORE = 20.0;
    private static final double MAX_SCORE = 100.0;

    @Override
    public AiPostSpecResponse analyzeSpec(AiPostSpecRequest aiPostSpecRequest) {
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
        return MIN_SCORE + (MAX_SCORE - MIN_SCORE) * random.nextDouble();
    }


    private double calculateAverageScore(AiPostSpecResponse response) {
        return (response.getAcademicScore() +
                response.getWorkExperienceScore() +
                response.getCertificationScore() +
                response.getLanguageProficiencyScore() +
                response.getExtracurricularScore()) / 5.0;
    }


    private double adjustTotalScore(double avgScore) {
        double adjustment = (random.nextDouble() * 10.0) - 5.0;
        double adjusted = avgScore + adjustment;

        adjusted = guaranteeBetweenLowestAndHighest(adjusted);
        return adjusted;
    }

    private static double guaranteeBetweenLowestAndHighest(double adjusted) {
        return Math.min(Math.max(adjusted, MIN_SCORE), MAX_SCORE);
    }
}