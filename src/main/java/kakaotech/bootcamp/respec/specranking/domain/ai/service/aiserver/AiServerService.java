package kakaotech.bootcamp.respec.specranking.domain.ai.service.aiserver;

import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;

public interface AiServerService {
    AiPostSpecResponse call(AiPostSpecRequest aiPostSpecRequest);
}
