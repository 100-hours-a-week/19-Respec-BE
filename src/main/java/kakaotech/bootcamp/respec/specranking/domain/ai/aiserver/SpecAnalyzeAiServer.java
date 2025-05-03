package kakaotech.bootcamp.respec.specranking.domain.ai.aiserver;

import kakaotech.bootcamp.respec.specranking.domain.ai.dto.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.response.AiPostSpecResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Profile("ai")
@RequiredArgsConstructor
@Slf4j
public class SpecAnalyzeAiServer implements AiServer {

    private final WebClient aiServerWebClient;

    @Value("${ai.server.url.path}")
    private String urlPath;

    @Override
    public AiPostSpecResponse analyzeSpec(AiPostSpecRequest request) {
        return aiServerWebClient.post()
                .uri(urlPath)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        res -> res.bodyToMono(String.class)
                                .doOnNext(body -> log.error("AI {} body ▶ {}", res.statusCode(), body))
                                .map(body -> new IllegalStateException(
                                        "AI server error (" + res.statusCode() + "): " + body))
                )
                .bodyToMono(AiPostSpecResponse.class)
                .block();
    }
}
