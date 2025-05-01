package kakaotech.bootcamp.respec.specranking.domain.ai.service;

import jakarta.transaction.Transactional;
import kakaotech.bootcamp.respec.specranking.domain.ai.aiserver.AiServer;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AiService {

    private final AiServer aiServer;

    public AiPostSpecResponse analyzeSpec(AiPostSpecRequest request) {
        return aiServer.analyzeSpec(request);
    }
}
