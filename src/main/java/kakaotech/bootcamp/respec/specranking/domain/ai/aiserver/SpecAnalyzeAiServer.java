package kakaotech.bootcamp.respec.specranking.domain.ai.aiserver;

import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Profile("!dev")
@RequiredArgsConstructor
public class SpecAnalyzeAiServer implements AiServer {

    private final WebClient aiServerWebClient;

    @Value("${ai.server.url.path}")
    private String urlPath;

    @Override
    public AiPostSpecResponse analyzeSpec(AiPostSpecRequest request) {
        return aiServerWebClient.post()
                .uri(urlPath)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiPostSpecResponse.class)
                .block();
    }
}
