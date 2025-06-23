package kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.aiserver;

import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.ai.request.AiPostResumeRequest;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.ai.request.AiPostSpecRequest;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.ai.response.AiPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.ai.response.AiPostSpecResponse;
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

    @Value("${ai.server.url.spec.path}")
    private String specPath;

    @Value("${ai.server.url.resume.path}")
    private String resumePath;

    @Override
    public AiPostSpecResponse analyzeSpec(AiPostSpecRequest request) {
        return aiServerWebClient.post()
                .uri(specPath)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        res -> res.bodyToMono(String.class)
                                .doOnNext(body -> log.error("spec AI {} body ▶ {}", res.statusCode(), body))
                                .map(body -> new IllegalStateException(
                                        "spec AI server error (" + res.statusCode() + "): " + body))
                )
                .bodyToMono(AiPostSpecResponse.class)
                .block();
    }

    @Override
    public AiPostResumeResponse analyzeResume(AiPostResumeRequest aiPostResumeRequest) {
        return aiServerWebClient.post()
                .uri(resumePath)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(aiPostResumeRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        res -> res.bodyToMono(String.class)
                                .doOnNext(body -> log.error("resume AI {} body ▶ {}", res.statusCode(), body))
                                .map(body -> new IllegalStateException(
                                        "resume AI server error (" + res.statusCode() + "): " + body))
                )
                .bodyToMono(AiPostResumeResponse.class)
                .block();
    }
}
