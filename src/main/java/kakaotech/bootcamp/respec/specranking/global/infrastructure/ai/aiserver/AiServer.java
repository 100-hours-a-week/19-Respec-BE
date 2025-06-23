package kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.aiserver;

import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.request.AiPostResumeRequest;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.response.AiPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.response.AiPostSpecResponse;

public interface AiServer {
    AiPostSpecResponse analyzeSpec(AiPostSpecRequest aiPostSpecRequest);

    AiPostResumeResponse analyzeResume(AiPostResumeRequest aiPostResumeRequest);
}
