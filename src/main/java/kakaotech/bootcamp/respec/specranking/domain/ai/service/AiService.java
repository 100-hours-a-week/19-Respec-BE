package kakaotech.bootcamp.respec.specranking.domain.ai.service;

import kakaotech.bootcamp.respec.specranking.domain.ai.aiserver.AiServer;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request.AiPostResumeRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.response.AiPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.response.AiPostSpecResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiServer aiServer;

    public AiPostSpecResponse analyzeSpec(AiPostSpecRequest request) {
        return aiServer.analyzeSpec(request);
    }

    public AiPostResumeResponse analyzeResume(AiPostResumeRequest request) {
        return aiServer.analyzeResume(request);
    }
}
