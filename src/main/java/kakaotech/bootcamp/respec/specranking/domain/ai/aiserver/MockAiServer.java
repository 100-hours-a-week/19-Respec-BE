package kakaotech.bootcamp.respec.specranking.domain.ai.aiserver;

import java.util.Random;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;
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

    private AiPostSpecResponse createMockAiResponse(AiPostSpecRequest aiPostSpecRequest) {
        AiPostSpecResponse response = new AiPostSpecResponse();

        response.setNickname(aiPostSpecRequest.getNickname());
        response.setEducationScore(generateRandomScore());
        response.setWorkExperienceScore(generateRandomScore());
        response.setCertificationScore(generateRandomScore());
        response.setLanguageSkillScore(generateRandomScore());
        response.setActivityNetworkingScore(generateRandomScore());

        Double avgScore = calculateAverageScoreWithBasicField(response);
        Double totalScore = adjustTotalScore(avgScore);
        response.setTotalScore(totalScore);
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
}