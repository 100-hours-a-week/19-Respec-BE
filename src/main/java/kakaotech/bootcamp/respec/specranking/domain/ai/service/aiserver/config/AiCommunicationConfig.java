package kakaotech.bootcamp.respec.specranking.domain.ai.service.aiserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("!dev")
public class AiCommunicationConfig {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Bean
    public WebClient aiServerWebClient() {
        return WebClient.builder()
                .baseUrl(aiServerUrl)
                .build();
    }

}
