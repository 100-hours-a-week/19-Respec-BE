package kakaotech.bootcamp.respec.specranking.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("!local")
public class EC2MetaDataConfig {
    private static final String EC2_METADATA_BASE_URL = "http://169.254.169.254/latest/meta-data";

    @Bean
    public WebClient ec2MetadataWebClient() {
        return WebClient.builder()
                .baseUrl(EC2_METADATA_BASE_URL)
                .build();
    }
}
