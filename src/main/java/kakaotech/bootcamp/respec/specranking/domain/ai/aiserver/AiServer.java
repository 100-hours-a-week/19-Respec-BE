package kakaotech.bootcamp.respec.specranking.domain.ai.aiserver;

import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request.AiPostResumeRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.response.AiPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.response.AiPostSpecResponse;

public interface AiServer {
    AiPostSpecResponse analyzeSpec(AiPostSpecRequest aiPostSpecRequest);

    AiPostResumeResponse analyzeResume(AiPostResumeRequest aiPostResumeRequest);
}
