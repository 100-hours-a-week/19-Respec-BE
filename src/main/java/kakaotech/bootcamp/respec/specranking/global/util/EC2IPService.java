package kakaotech.bootcamp.respec.specranking.global.util;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Profile("!local")
@RequiredArgsConstructor
public class EC2IPService implements IPService {

    private final WebClient ec2MetadataWebClient;
    private final ServletWebServerApplicationContext webServerContext;

    public String loadEC2PrivateAddress() {
        return fetchPrivateIPWithIMDSv2() + ":" + getPrivatePort();
    }

    private String fetchPrivateIPWithIMDSv2() {
        String token = fetchIMDSToken();

        return ec2MetadataWebClient
                .get()
                .uri("http://169.254.169.254/latest/meta-data/local-ipv4")
                .header("X-aws-ec2-metadata-token", token)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    private String fetchIMDSToken() {
        return ec2MetadataWebClient
                .put()
                .uri("http://169.254.169.254/latest/api/token")
                .header("X-aws-ec2-metadata-token-ttl-seconds", "21600")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    private int getPrivatePort() {
        return webServerContext.getWebServer().getPort();
    }
}
