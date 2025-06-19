package kakaotech.bootcamp.respec.specranking.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("!local")
public class EC2MetaDataConfig {

    @Bean
    public WebClient ec2MetadataWebClient() {
        return WebClient.builder()
                .baseUrl("")
                .build();
    }
}
