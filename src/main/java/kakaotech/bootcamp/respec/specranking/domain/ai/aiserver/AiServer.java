package kakaotech.bootcamp.respec.specranking.domain.ai.aiserver;

import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;

public interface AiServer {
    AiPostSpecResponse analyzeSpec(AiPostSpecRequest aiPostSpecRequest);
}
